<template>
    <span class="flex gap-8 relative">
        <RouterLink to="/cart">
            <div
                class="bg-gray-200 py-2 px-4 rounded-md cursor-pointer hover:shadow-md duration-200 gap-2 flex items-center"
            >
                <fa icon="cart-shopping" />
                <span>Cart</span>
            </div>
        </RouterLink>
        <div
            v-if="qty > 0"
            class="bg-red-500 text-white rounded-full w-4 h-4 absolute top-[-5px] right-[-5px] flex items-center justify-center"
        >
            <span class="text-xs">{{ qty }}</span>
        </div>
    </span>
</template>

<script setup>
const props = defineProps({
    qty: {
        type: Number,
        required: false,
    },
})
</script>