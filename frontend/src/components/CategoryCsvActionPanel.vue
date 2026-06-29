<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  selectedFile: File | null
  isUploading: boolean
  isDownloading: boolean
}>()

const emit = defineEmits<{
  fileSelected: [file: File | null]
  download: []
  upload: []
}>()

const fileInput = ref<HTMLInputElement | null>(null)

watch(
  () => props.selectedFile,
  (selectedFile) => {
    if (!selectedFile && fileInput.value) {
      fileInput.value.value = ''
    }
  }
)

function handleFileChange(event: Event): void {
  const input = event.target as HTMLInputElement
  emit('fileSelected', input.files?.[0] ?? null)
}
</script>

<template>
  <section class="action-panel" aria-label="CSV 操作">
    <div class="action-group">
      <button
        class="button button-secondary"
        type="button"
        :disabled="props.isDownloading"
        @click="emit('download')"
      >
        {{ props.isDownloading ? 'ダウンロード中' : 'CSV ダウンロード' }}
      </button>
    </div>

    <div class="upload-controls">
      <label class="file-picker">
        <span>CSV ファイル</span>
        <input ref="fileInput" type="file" accept=".csv,text/csv" @change="handleFileChange" />
      </label>
      <p class="selected-file" :title="props.selectedFile?.name">
        {{ props.selectedFile?.name ?? '未選択' }}
      </p>
      <button
        class="button button-primary"
        type="button"
        :disabled="!props.selectedFile || props.isUploading"
        @click="emit('upload')"
      >
        {{ props.isUploading ? 'アップロード中' : 'CSV アップロード' }}
      </button>
    </div>
  </section>
</template>
