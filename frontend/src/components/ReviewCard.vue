<template>
  <div class="border-2 rounded-md p-4 w-full mb-4 shadow-md h-fit min-w-48">
      <div class="flex justify-between">
          <p>Order ID: #{{ review.order.id.slice(0, 4) }}</p>
          <div class="flex gap-4 items-center">
              <button 
              v-if="loginUserId === review.customer.id || role === 'ADMIN'"
              class="bg-red-500 text-white px-2 py-1 rounded-md"
              @click="deleteReview(review.id)"
          >
              Delete
          </button>
          </div>
      </div>
      <p>Customer Name: {{ review.customer.username }}</p>

      <p>
          Review Date:
          {{
              review.createdAt.split('T')[0] +
              ' | ' +
              review.createdAt.split('T')[1].slice(0, 5)
          }}
      </p>
      <fa
          v-for="i in 5"
          :key="i"
          icon="star"
          :class="i <= review.rating ? 'text-yellow-500' : 'text-gray-300'"
          class="text-lg"
      />
      <p class="bg-gray-200 py-4 px-2 rounded-md h-fit">
          {{ review.comment }}
      </p>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, onMounted } from 'vue'
import reviewApi from '@/api/reviewApi.js'

const props = defineProps<{ review }>()
const loginUserId = ref<string>('')
const role = ref<string>('')

const deleteReview = async (id) => {
  try {
      await reviewApi.deleteReview(id)
      console.log('Review deleted')
      window.location.reload()
  } catch (error) {
      console.error('Error deleting review:', error)
  }
}

</script>

<style scoped>
.star {
  color: grey;
}
</style>