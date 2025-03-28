<template>
    <div
        class="flex justify-between items-center flex-col w-60 h-96 rounded-lg shadow-lg bg-gray-100 p-4"
    >
        <div class="w-full h-3/5 relative">
            <img
                :src="promotionData.imagePath"
                alt="Promotion Image"
                class="w-full h-full object-cover rounded-t-lg"
            />
            <div
                class="absolute inset-0 bg-gradient-to-b from-transparent to-gray-800 opacity-50 bottom-0"
            ></div>
        </div>
        <div class="flex flex-col items-center justify-center h-2/5 w-full gap-2 mt-2">
            <span class="font-bold text-lg text-gray-800">{{ promotionData.name }}</span>
            <span class="text-yellow-600 font-bold text-xl">
                {{promotionData.price}}฿
            </span>
            <p class="text-gray-600 text-sm">
                {{ promotionData.startDate }} - {{ promotionData.endDate }}
            </p>
            <div class="flex gap-3 mt-2">
                <button
                    class="px-2 rounded-md bg-yellow-400 shadow-md hover:bg-yellow-500 duration-150"
                    @click="addToCart(promotionData)"
                >
                    Add to Cart
                </button>
                <button
                    class="px-2 py-2 rounded-md bg-blue-400 shadow-md hover:bg-blue-500 duration-150"
                    @click="toggleModal"
                >
                    Description
                </button>
            </div>
        </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
        <div class="bg-white p-6 rounded-lg shadow-lg w-1/3">
            <h2 class="text-2xl font-bold mb-4 text-gray-800">{{ promotionData.name }}</h2>
            <p class="text-gray-700">{{ promotionData.description }}</p>
            <button
                class="mt-6 px-4 py-2 bg-red-400 rounded-md shadow-md hover:bg-red-500 duration-150"
                @click="toggleModal"
            >
                Close
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { foodsStore } from '@/stores/cart';

const props = defineProps({
    promotionData: {
        type: Object,
        required: true,
    },
});

const foodStore = foodsStore();
const showModal = ref(false);

const addToCart = (food) => {
    console.log(food.max);
    foodStore.addToCart(food, 'promotion');
};

const toggleModal = () => {
    showModal.value = !showModal.value;
};
</script>