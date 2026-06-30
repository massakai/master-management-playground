import { mount } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { nextTick } from 'vue'
import CategoryPage from './CategoryPage.vue'
import {
  ApiError,
  fetchCategories,
  uploadCategoriesCsv
} from '../api/categoryApi'
import type {
  CategoryCsvUploadSuccessResponse,
  CategoryListResponse
} from '../types/category'

vi.mock('../api/categoryApi', async () => {
  const actual =
    await vi.importActual<typeof import('../api/categoryApi')>('../api/categoryApi')

  return {
    ...actual,
    downloadCategoriesCsv: vi.fn(),
    fetchCategories: vi.fn(),
    uploadCategoriesCsv: vi.fn()
  }
})

const fetchCategoriesMock = vi.mocked(fetchCategories)
const uploadCategoriesCsvMock = vi.mocked(uploadCategoriesCsv)

describe('CategoryPage', () => {
  beforeEach(() => {
    fetchCategoriesMock.mockResolvedValue(categoryListResponse([]))
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('初期表示で画面タイトル、CSV 操作、読み込み状態を表示する', async () => {
    const deferred = createDeferred<CategoryListResponse>()
    fetchCategoriesMock.mockReturnValue(deferred.promise)

    const wrapper = mount(CategoryPage)
    await nextTick()

    expect(wrapper.text()).toContain('商品カテゴリマスター管理')
    expect(wrapper.text()).toContain('CSV ダウンロード')
    expect(wrapper.text()).toContain('CSV アップロード')
    expect(wrapper.text()).toContain('読み込み中です。')

    deferred.resolve(categoryListResponse([]))
    await flushAsync()
  })

  it('API から取得したカテゴリ一覧を表示する', async () => {
    fetchCategoriesMock.mockResolvedValue(
      categoryListResponse([
        {
          categoryCode: 'FOOD',
          categoryName: '食品',
          displayOrder: 10,
          isActive: true,
          description: '日配品を含む'
        },
        {
          categoryCode: 'DISCONTINUED',
          categoryName: '終売品',
          displayOrder: 99,
          isActive: false,
          description: null
        }
      ])
    )

    const wrapper = mount(CategoryPage)
    await flushAsync()

    expect(wrapper.text()).toContain('2 件')
    expect(wrapper.text()).toContain('FOOD')
    expect(wrapper.text()).toContain('食品')
    expect(wrapper.text()).toContain('有効')
    expect(wrapper.text()).toContain('DISCONTINUED')
    expect(wrapper.text()).toContain('終売品')
    expect(wrapper.text()).toContain('無効')
  })

  it('CSV アップロード成功時にフィードバックを表示して一覧を再読込する', async () => {
    const uploadResponse: CategoryCsvUploadSuccessResponse = {
      success: true,
      message: 'CSV アップロードが完了しました。',
      updatedCount: 1
    }
    const csvFile = new File(['categoryCode,categoryName\nDRINK,飲料'], 'categories.csv', {
      type: 'text/csv'
    })

    fetchCategoriesMock
      .mockResolvedValueOnce(categoryListResponse([]))
      .mockResolvedValueOnce(
        categoryListResponse([
          {
            categoryCode: 'DRINK',
            categoryName: '飲料',
            displayOrder: 20,
            isActive: true,
            description: 'CSV から更新'
          }
        ])
      )
    uploadCategoriesCsvMock.mockResolvedValue(uploadResponse)

    const wrapper = mount(CategoryPage)
    await flushAsync()

    const fileInput = wrapper.find<HTMLInputElement>('input[type="file"]')
    Object.defineProperty(fileInput.element, 'files', {
      value: [csvFile],
      configurable: true
    })
    await fileInput.trigger('change')
    await wrapper.find('button.button-primary').trigger('click')
    await flushAsync()

    expect(uploadCategoriesCsvMock).toHaveBeenCalledWith(csvFile)
    expect(fetchCategoriesMock).toHaveBeenCalledTimes(2)
    expect(wrapper.text()).toContain('CSV アップロードが完了しました。（更新件数: 1 件）')
    expect(wrapper.text()).toContain('DRINK')
    expect(wrapper.text()).toContain('飲料')
    expect(wrapper.text()).toContain('未選択')
  })

  it('CSV バリデーションエラーを表示する', async () => {
    const csvFile = new File(['bad csv'], 'invalid.csv', { type: 'text/csv' })
    uploadCategoriesCsvMock.mockRejectedValue(
      new ApiError('CSV の内容にエラーがあります。', 400, {
        success: false,
        message: 'CSV の内容にエラーがあります。',
        errorCount: 1,
        errors: [
          {
            rowNumber: 3,
            field: 'categoryName',
            code: 'REQUIRED',
            message: 'カテゴリ名は必須です。'
          }
        ]
      })
    )

    const wrapper = mount(CategoryPage)
    await flushAsync()

    const fileInput = wrapper.find<HTMLInputElement>('input[type="file"]')
    Object.defineProperty(fileInput.element, 'files', {
      value: [csvFile],
      configurable: true
    })
    await fileInput.trigger('change')
    await wrapper.find('button.button-primary').trigger('click')
    await flushAsync()

    expect(fetchCategoriesMock).toHaveBeenCalledTimes(1)
    expect(wrapper.text()).toContain('CSV の内容にエラーがあります。')
    expect(wrapper.text()).toContain('CSV エラー')
    expect(wrapper.text()).toContain('3')
    expect(wrapper.text()).toContain('categoryName')
    expect(wrapper.text()).toContain('REQUIRED')
    expect(wrapper.text()).toContain('カテゴリ名は必須です。')
  })

  it('一覧取得 API エラー時にエラーメッセージを表示する', async () => {
    fetchCategoriesMock.mockRejectedValue(new ApiError('Internal Server Error', 500, ''))

    const wrapper = mount(CategoryPage)
    await flushAsync()

    expect(wrapper.text()).toContain('通信に失敗しました。時間をおいて再度お試しください。')
    expect(wrapper.text()).toContain('商品カテゴリは登録されていません。')
  })
})

function categoryListResponse(
  categories: CategoryListResponse['categories']
): CategoryListResponse {
  return { categories }
}

function createDeferred<T>(): {
  promise: Promise<T>
  resolve: (value: T) => void
} {
  let resolve!: (value: T) => void
  const promise = new Promise<T>((promiseResolve) => {
    resolve = promiseResolve
  })

  return { promise, resolve }
}

async function flushAsync(): Promise<void> {
  await Promise.resolve()
  await Promise.resolve()
}
