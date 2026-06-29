<script setup lang="ts">
import type { CategoryViewModel } from '../types/category'

defineProps<{
  categories: CategoryViewModel[]
  isLoading: boolean
}>()
</script>

<template>
  <section class="table-section" aria-labelledby="categories-title">
    <div class="section-header">
      <h2 id="categories-title">商品カテゴリ一覧</h2>
      <span class="count-badge">{{ categories.length }} 件</span>
    </div>

    <div class="table-scroll">
      <table>
        <thead>
          <tr>
            <th scope="col">カテゴリコード</th>
            <th scope="col">カテゴリ名</th>
            <th scope="col">表示順</th>
            <th scope="col">有効フラグ</th>
            <th scope="col">説明</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="isLoading">
            <td class="table-empty" colspan="5">読み込み中です。</td>
          </tr>
          <tr v-else-if="categories.length === 0">
            <td class="table-empty" colspan="5">商品カテゴリは登録されていません。</td>
          </tr>
          <tr v-for="category in categories" v-else :key="category.categoryCode">
            <td>
              <code>{{ category.categoryCode }}</code>
            </td>
            <td>{{ category.categoryName }}</td>
            <td class="number-cell">{{ category.displayOrder }}</td>
            <td>
              <span class="status-pill" :class="{ inactive: !category.isActive }">
                {{ category.isActive ? '有効' : '無効' }}
              </span>
            </td>
            <td>{{ category.description ?? '' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
