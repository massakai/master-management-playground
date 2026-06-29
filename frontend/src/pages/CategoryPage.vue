<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  ApiError,
  downloadCategoriesCsv,
  fetchCategories,
  isCategoryCsvUploadErrorResponse,
  uploadCategoriesCsv
} from '../api/categoryApi'
import CategoryCsvActionPanel from '../components/CategoryCsvActionPanel.vue'
import CategoryCsvErrorTable from '../components/CategoryCsvErrorTable.vue'
import CategoryFeedbackMessage from '../components/CategoryFeedbackMessage.vue'
import CategoryPageHeader from '../components/CategoryPageHeader.vue'
import CategoryTable from '../components/CategoryTable.vue'
import type { CategoryViewModel, CsvValidationErrorViewModel } from '../types/category'

const categories = ref<CategoryViewModel[]>([])
const selectedFile = ref<File | null>(null)
const isLoadingList = ref(false)
const isUploading = ref(false)
const isDownloading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')
const validationErrors = ref<CsvValidationErrorViewModel[]>([])

onMounted(() => {
  void loadCategories()
})

async function loadCategories(): Promise<void> {
  isLoadingList.value = true
  errorMessage.value = ''

  try {
    const response = await fetchCategories()
    categories.value = response.categories
  } catch {
    errorMessage.value = '通信に失敗しました。時間をおいて再度お試しください。'
  } finally {
    isLoadingList.value = false
  }
}

async function handleDownload(): Promise<void> {
  isDownloading.value = true
  clearMessages()

  try {
    const csv = await downloadCategoriesCsv()
    saveBlob(csv, 'product-categories.csv')
  } catch {
    errorMessage.value = 'CSV のダウンロードに失敗しました。時間をおいて再度お試しください。'
  } finally {
    isDownloading.value = false
  }
}

async function handleUpload(): Promise<void> {
  if (!selectedFile.value) {
    errorMessage.value = 'CSV ファイルを選択してください。'
    return
  }

  isUploading.value = true
  clearMessages()

  try {
    const response = await uploadCategoriesCsv(selectedFile.value)
    successMessage.value = `${response.message}（更新件数: ${response.updatedCount} 件）`
    selectedFile.value = null
    await loadCategories()
  } catch (error) {
    handleUploadError(error)
  } finally {
    isUploading.value = false
  }
}

function handleUploadError(error: unknown): void {
  if (error instanceof ApiError && error.status === 400) {
    if (isCategoryCsvUploadErrorResponse(error.body)) {
      errorMessage.value = error.body.message
      validationErrors.value = error.body.errors
      return
    }

    errorMessage.value = error.message
    return
  }

  if (error instanceof ApiError && error.status >= 500) {
    errorMessage.value = 'システムエラーが発生しました。'
    return
  }

  errorMessage.value = '通信に失敗しました。時間をおいて再度お試しください。'
}

function clearMessages(): void {
  successMessage.value = ''
  errorMessage.value = ''
  validationErrors.value = []
}

function saveBlob(blob: Blob, fileName: string): void {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')

  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
</script>

<template>
  <main class="page-shell">
    <CategoryPageHeader />

    <CategoryCsvActionPanel
      :selected-file="selectedFile"
      :is-uploading="isUploading"
      :is-downloading="isDownloading"
      @file-selected="selectedFile = $event"
      @download="handleDownload"
      @upload="handleUpload"
    />

    <CategoryFeedbackMessage
      :success-message="successMessage"
      :error-message="errorMessage"
    />

    <CategoryCsvErrorTable :errors="validationErrors" />

    <CategoryTable :categories="categories" :is-loading="isLoadingList" />
  </main>
</template>
