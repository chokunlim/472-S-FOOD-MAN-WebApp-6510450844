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
                            ? 'bg-yellow-300 flex justify-center rounded-md shadow-md'
                            : 'flex-start hover:bg-yellow-100 duration-300 rounded-md',
                    ]"
                    @click="selectItem(index)"
                >
                    <router-link
                        :to="item.path"
                        class="flex items-center w-full rounded-md px-2"
                        exact-active-class="bg-yellow-300 rounded-md"
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
    console.log('asdadsasd', res.data)
})

const menuItems = [
    {
        label: 'Menu',
        icon: 'bars',
        path: '/food',
        role: ['ADMIN', 'CUSTOMER', 'MANAGER'],
    },
    {
        label: 'Order',
        icon: 'book',
        path: '/order',
        role: ['ADMIN', 'CUSTOMER'],
    },
    {
        label: 'To Deliver',
        icon: 'book',
        path: '/orderforrider',
        role: ['RIDER'],
    },
    {
        label: 'History',
        icon: 'book',
        path: '/orderhistory',
        role: ['RIDER'],
    },
    {
        label: 'Ingredient',
        icon: 'book',
        path: '/ingredient',
        role: ['ADMIN'],
    },
    {
        label: 'Dashboard',
        icon: 'chart-line',
        path: '/dashboard',
        role: ['ADMIN'],
    },
]

// user มองเห็น tab sidebar ได้ตาม role ที่กำหนด
const filteredMenuItems = computed(() => {
    if (!role.value) return []
    console.log(role.value)
    return menuItems.filter((item) =>
        item.role.includes(role.value.toUpperCase())
    )
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