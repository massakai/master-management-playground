<script setup lang="ts">
import type { CsvValidationErrorViewModel } from '../types/category'

defineProps<{
  errors: CsvValidationErrorViewModel[]
}>()
</script>

<template>
  <section v-if="errors.length > 0" class="table-section" aria-labelledby="csv-errors-title">
    <div class="section-header">
      <h2 id="csv-errors-title">CSV エラー</h2>
      <span class="count-badge">{{ errors.length }} 件</span>
    </div>
    <div class="table-scroll">
      <table>
        <thead>
          <tr>
            <th scope="col">行番号</th>
            <th scope="col">項目名</th>
            <th scope="col">エラーコード</th>
            <th scope="col">エラーメッセージ</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(error, index) in errors" :key="`${error.rowNumber}-${error.field}-${index}`">
            <td>{{ error.rowNumber ?? '-' }}</td>
            <td>{{ error.field }}</td>
            <td>
              <code>{{ error.code }}</code>
            </td>
            <td>{{ error.message }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
