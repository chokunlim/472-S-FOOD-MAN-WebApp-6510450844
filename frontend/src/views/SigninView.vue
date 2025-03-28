<template>
    <div
        class="w-full h-screen bg-[url('../assets/bg2.png')] bg-no-repeat bg-cover flex justify-center items-center"
    >
        <div class="h-4/5 w-96 sm:w-3/5 md:w-3/6 bg-white rounded-lg">
            <form
                @submit.prevent="login"
                class="flex flex-col w-4/5 h-full m-auto gap-8 justify-center"
            >
                <div class="flex mx-auto justify-center">
                    <img src="../assets/food-icon.png" />
                    <span class="flex items-center text-2xl font-bold">
                        &nbsp;SuperDuperPOS</span
                    >
                </div>
                <input
                    v-model="username"
                    type="text"
                    class="input"
                    placeholder="Username..."
                />
                <input
                    v-model="password"
                    type="password"
                    class="input"
                    placeholder="Password..."
                />
                <button type="submit" class="auth-button">Sign In</button>
                <div class="flex justify-center">
                    <p class="text-gray-500">Don't have an account? &nbsp;</p>
                    <p class="text-blue-500 hover:underline">
                        <a href="/signup">Go to registration</a>
                    </p>
                </div>
                <div v-if="errorMessage" class="text-red-500">
                    {{ errorMessage }}
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authApi from '@/api/authApi'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const login = async () => {
    errorMessage.value = ''

    try {
        const { data: response } = await authApi.signIn({
            username: username.value,
            password: password.value,
        })

        const token = response.data.token // jwt token
        if (token) {
            localStorage.setItem('token', token) // Save token in localStorage

            if (response.data.role === 'RIDER') {
                router.push('/orderforrider')
            } else {
                router.push('/food')
            }
        }
    } catch (error) {
        console.error('Login failed:', error.response?.data || error.message)
        errorMessage.value = 'Login failed. Please try again.'
    }
}
</script>
