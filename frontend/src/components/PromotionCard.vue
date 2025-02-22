<template>
    <div
        class="flex justify-between items-center flex-col w-60 h-72 rounded-lg shadow-md bg-white"
    >
        <div class="w-full h-3/5 relative">
            <img
                :src="promotionData.imagePath"
                alt="Promotion Image"
                class="w-full h-full object-cover rounded-t-lg"
            />
            <div
                class="absolute inset-0 bg-gradient-to-b from-transparent to-white opacity-20 bottom-0"
            ></div>
        </div>
        <div class="flex flex-col items-center justify-center h-2/5 w-full gap-1">
            <span class="font-bold">{{ promotionData.name }}</span>

            {{ promotionData.description }}
            <span class="text-yellow-500 font-bold">
                {{promotionData.price}}฿
            </span>
            <p class="text-gray-500 text-xs">
                {{ promotionData.startDate }} - {{ promotionData.endDate }}
            </p>
           
        </div>
    </div>
</template>

<script setup>
const props = defineProps({
    promotionData: {
        type: Object,
        required: true,
    },
});
</script>
