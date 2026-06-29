<script setup lang="ts">
defineProps<{
  successMessage: string
  errorMessage: string
}>()
</script>

<template>
  <div v-if="successMessage || errorMessage" class="message-stack" aria-live="polite">
    <p v-if="successMessage" class="feedback-message feedback-success">
      {{ successMessage }}
    </p>
    <p v-if="errorMessage" class="feedback-message feedback-error">
      {{ errorMessage }}
    </p>
  </div>
</template>
