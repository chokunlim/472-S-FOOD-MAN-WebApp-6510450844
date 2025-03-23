<script setup>
import orderApi from '@/api/orderApi'
import Sidebar from '@/components/Sidebar.vue'
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-8 px-8 flex flex-col gap-6 bg-gray-100 h-full min-h-screen items-center justify-center"
        >
            <div
                class="flex flex-col items-center bg-white p-8 rounded-lg shadow-lg gap-6"
            >
                <h1 class="text-red-500 text-4xl font-bold">Payment Failed</h1>
                <div>
                    <img
                        src="@/assets/cancel_icon.png"
                        alt="Cancel Icon"
                        class="w-20 h-20"
                    />
                </div>
                <p class="text-lg text-gray-700 text-center">
                    Unfortunately, your payment could not be processed. Please
                    try again!
                </p>
                <router-link
                    to="/cart"
                    class="mt-4 bg-red-500 text-white py-2 px-4 rounded-lg shadow hover:bg-red-200 transition-all duration-200"
                >
                    Try again!
                </router-link>
            </div>
        </main>
    </div>
</template>