<template>
    <aside
        class="flex flex-col justify-between items-center h-screen w-56 py-4 border-r-2"
    >
        <div class="flex flex-col gap-12">
            <div class="flex items-center">
                <img src="../assets/food-icon.png" width="42px" />
                <router-link to="/food">
                    <span class="flex font-bold text-lg"
                        >&nbsp;SuperDuperPOS</span
                    >
                </router-link>
            </div>
            <ul class="flex flex-col gap-8">
                <li
                    v-for="(item, index) in filteredMenuItems"
                    :key="index"
                    :class="[
                        'list',
                        selectedItem === index || $route.path === item.path
                            ? 'bg-yellow-300 flex justify-center'
                            : 'flex-start',
                    ]"
                    @click="selectItem(index)"
                >
                    <router-link
                        :to="item.path"
                        class="flex items-center w-full rounded-md px-2"
                        exact-active-class="bg-yellow-300"
                    >
                        <div class="w-12">
                            <fa :icon="item.icon" />
                        </div>
                        <span class="text-lg w-full pr-4 py-1 rounded-md">
                            {{ item.label }}
                        </span>
                    </router-link>
                </li>
            </ul>
        </div>
        <div class="mt-32">
            <span
                class="px-16 py-2 bg-gray-200 rounded-md cursor-pointer shadow-md"
                @click="logout"
                >Logout</span
            >
        </div>
    </aside>
</template>

<script setup>
import userApi from '@/api/userApi'
import router from '@/router'
import { onMounted, ref, computed } from 'vue'

const role = ref('')
const selectedItem = ref(null)

onMounted(async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role
})

const menuItems = [
    { label: 'Menu', icon: 'bars', path: '/food' },
    { label: 'Order', icon: 'book', path: '/order' },
    { label: 'Ingredient', icon: 'book', path: '/ingredient', role: 'ADMIN' },
    { label: 'Review', icon: 'book', path: '/reviewlistview', role: 'ADMIN' },
    { label: 'Dashboard', icon: 'chart-line', path: '/dashboard', role: 'ADMIN' },
]

// Filter menu items based on the user's role
const filteredMenuItems = computed(() => {
    return menuItems.filter((item) => !item.role || item.role === role.value)
})

const selectItem = (index) => {
    selectedItem.value = index
}

const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('carts')
    router.push('/signin')
}
</script>

<style scoped>
.list {
    @apply flex justify-start items-center cursor-pointer rounded-md duration-200;
}

.list:hover {
    @apply bg-yellow-300 shadow-md;
}
</style>
