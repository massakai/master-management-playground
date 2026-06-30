import { expect, test } from '@playwright/test'

const invalidCsv = `category_code,category_name,display_order,is_active,description
FOOD,食品,10,yes,食品カテゴリ
`

const validCsv = `category_code,category_name,display_order,is_active,description
FOOD,食品・冷凍,10,true,冷凍食品を含む
BOOK,書籍,20,true,書籍カテゴリ
GIFT,ギフト,40,false,ギフトカテゴリ
`

test('商品カテゴリ一覧、CSV ダウンロード、CSV アップロードをブラウザから操作できる', async ({
  page
}) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: '商品カテゴリマスター管理' })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'FOOD' })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'BOOK' })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'DAILY' })).toBeVisible()

  const downloadPromise = page.waitForEvent('download')
  await page.getByRole('button', { name: 'CSV ダウンロード' }).click()
  const download = await downloadPromise
  expect(download.suggestedFilename()).toBe('product-categories.csv')

  await page.getByLabel('CSV ファイル').setInputFiles({
    name: 'invalid-categories.csv',
    mimeType: 'text/csv',
    buffer: Buffer.from(invalidCsv, 'utf-8')
  })
  await page.getByRole('button', { name: 'CSV アップロード' }).click()

  await expect(page.getByText('CSV にエラーがあります。')).toBeVisible()
  await expect(page.getByRole('heading', { name: 'CSV エラー' })).toBeVisible()
  await expect(page.getByText('true または false を入力してください。')).toBeVisible()

  await page.getByLabel('CSV ファイル').setInputFiles({
    name: 'valid-categories.csv',
    mimeType: 'text/csv',
    buffer: Buffer.from(validCsv, 'utf-8')
  })
  await page.getByRole('button', { name: 'CSV アップロード' }).click()

  await expect(page.getByText('商品カテゴリを更新しました。')).toBeVisible()
  await expect(page.getByRole('heading', { name: 'CSV エラー' })).toHaveCount(0)
  await expect(page.getByRole('cell', { name: '食品・冷凍' })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'ギフト', exact: true })).toBeVisible()
  await expect(page.getByRole('cell', { name: 'DAILY' })).toHaveCount(0)
  await expect(page.locator('section[aria-labelledby="categories-title"] .count-badge')).toHaveText(
    '3 件'
  )
})
