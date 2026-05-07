import { createRouter, createWebHistory } from 'vue-router'
import NotFoundPage from '../pages/NotFound.vue'
import LoginPage from '../pages/auth/LoginPage.vue'
import RegisterPage from '../pages/auth/RegisterPage.vue'
import ClubPlazaPage from '../pages/student/ClubPlazaPage.vue'
import ClubDetailPage from '../pages/student/ClubDetailPage.vue'
import CreateClubPage from '../pages/student/CreateClubPage.vue'
import MyCenterPage from '../pages/student/MyCenterPage.vue'
import ClubAdminDashboardPage from '../pages/admin/ClubAdminDashboardPage.vue'
import PlatformAdminDashboardPage from '../pages/admin/PlatformAdminDashboardPage.vue'
import { useAuthStore } from '../stores/auth'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/register', name: 'register', component: RegisterPage },

    { path: '/clubs', name: 'club-plaza', component: ClubPlazaPage },
    { path: '/clubs/create', name: 'club-create', component: CreateClubPage, meta: { requiresAuth: true } },
    { path: '/clubs/:clubId', name: 'club-detail', component: ClubDetailPage },

    { path: '/me', name: 'me', component: MyCenterPage, meta: { requiresAuth: true } },

    { path: '/admin/club', name: 'admin-club', component: ClubAdminDashboardPage, meta: { requiresAuth: true } },
    { path: '/admin/platform', name: 'admin-platform', component: PlatformAdminDashboardPage, meta: { requiresAuth: true } },

    { path: '/dev/ping', name: 'dev-ping', component: () => import('../pages/Home.vue') },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFoundPage },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthed) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})
