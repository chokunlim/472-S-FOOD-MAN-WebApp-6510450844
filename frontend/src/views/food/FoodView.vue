<script setup>
import { computed, onMounted, ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import Card from '@/components/cards/Card.vue'
import Search from '@/components/Search.vue'
import { foodsStore } from '@/stores/cart'
import Cart from '@/components/Cart.vue'
import router from '@/router'
import userApi from '@/api/userApi'

const foodStore = foodsStore()
const searchQuery = ref('')
const role = ref('')

const fetchFoods = async () => {
    await foodStore.fetchFoods()
}

const filteredFoods = computed(() => {
    if (!searchQuery.value) return foodStore.foods
    return foodStore.foods.filter((food) =>
        food.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})

const getUser = async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role
}

const addfood = () => {
    router.push('/addfood')
}

onMounted(() => {
    fetchFoods()
    getUser()
})

</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 min-h-screen h-full"
        >
            <!-- search filter section  -->
            <section class="flex gap-4">
                <Search @update-search="searchQuery = $event" />
                <div class="flex items-center cursor-pointer">
                    <fa icon="bell" />
                </div>
                <Cart :qty="foodStore.cartQty" />
            </section>
            <!-- Name section  -->
            <section class="w-full h-12">
                <span class="font-bold text-3xl">Menu</span>
            </section>
            <section>
                <div
                    v-if="role === 'ADMIN'"
                    class="inline py-2 px-4 rounded-md items-center cursor-pointer bg-yellow-300 shadow-md"
                >
                    <span @click="addfood"><fa icon="add" /> Food </span>
                </div>
            </section>
            <!-- food list section  -->
            <section class="pb-12">
                <ul class="foods-grid">
                    <li v-for="food in filteredFoods" :key="food.id">
                        <Card :foodsData="food" />
                    </li>
                </ul>
                <p
                    v-if="foodStore.foods.length === 0"
                    class="mt-4 text-gray-500"
                >
                    No food available.
                </p>
            </section>
        </main>
    </div>
</template>
