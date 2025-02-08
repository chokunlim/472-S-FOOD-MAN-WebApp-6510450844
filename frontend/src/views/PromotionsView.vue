<script setup>
import { computed, onMounted, ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
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
                <span class="font-bold text-3xl">Promotions</span>
            </section>
            <section>
                <div
                    v-if="role === 'ADMIN'"
                    class="inline py-2 px-4 rounded-md items-center cursor-pointer bg-yellow-300 shadow-md"
                >
                    <span><fa icon="add" /> promotions </span>
                </div>
            </section>
        </main>
    </div>
</template>
