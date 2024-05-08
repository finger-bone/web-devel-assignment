import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { App, h } from 'vue'
import { BuildingLibraryIcon,
   BuildingOffice2Icon, 
   HomeIcon, 
   AcademicCapIcon, 
   UsersIcon, 
   BookOpenIcon,
   PencilSquareIcon,
   InformationCircleIcon,
   CalendarDaysIcon,
   ClockIcon
   } from '@heroicons/vue/24/solid'
import { setupRouterGuard } from './guard'

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard',
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: '/login',
    name: 'Login',
    meta: {
      hideInMenu: true,
    },
    component: () => import('@/views/sys/login/LoginIndex.vue'),
  },
  {
    path: '/reset',
    name: 'Reset',
    meta: {
      hideInMenu: true,
    },
    component: () => import('@/views/sys/login/reset-password.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: {
      hideInMenu: true,
    },
    component: () => import('@/views/sys/error-page/404Page.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    meta: {
      hideInMenu: true,
    },
    redirect: '/404',
  },
]

export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/dashboard',
    component: MainLayout,
    meta: {
      roles: ['admin'],
    },
    children: [
      {
        path: '',
        name: 'Dashboard',
        meta: {
          title: '首页',
          icon: () => h(HomeIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/dash-board.vue'),
      },
    ],
  },
  {
    path: '/student',
    name: '学员',
    meta: {
      title: '学员',
      icon: () => h(AcademicCapIcon, { class: 'w-4 h-4' }),
      roles: ['admin'],
    },
    component: MainLayout,
    children: [
      {
        path: '/student/management',
        name: '学员管理',
        meta: {
          title: '学员管理',
          icon: () => h(PencilSquareIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/student-management.vue'),
      },
      {
        path: '/student/statistics',
        name: '学员信息',
        meta: {
          title: '学员信息',
          icon: () => h(InformationCircleIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/student-statistics.vue'),
      },
    ],
  },
  {
    path: '/course',
    name: '课程',
    meta: {
      title: '课程',
      icon: () => h(BookOpenIcon, { class: 'w-4 h-4' }),
      roles: ['admin'],
    },
    component: MainLayout,
    children: [
      {
        path: '/course/management',
        name: '课程管理',
        meta: {
          title: '课程管理',
          icon: () => h(PencilSquareIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/course-management.vue'),
      },
      {
        path: '/course/assignment',
        name: '课程安排',
        meta: {
          title: '课程安排',
          icon: () => h(CalendarDaysIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/course-assignment.vue'),
      },
      {
        path: '/course/teacher',
        name: '教师时间表',
        meta: {
          title: '教师时间表',
          icon: () => h(ClockIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/teacher-schedule.vue'),
      }
    ],
  },
  {
    path: '/employee',
    name: '员工',
    meta: {
      title: '员工',
      icon: () => h(UsersIcon, { class: 'w-4 h-4' }),
      roles: ['admin'],
    },
    component: MainLayout,
    children: [
      {
        path: '/employee/management',
        name: '员工管理',
        meta: {
          title: '员工管理',
          icon: () => h(PencilSquareIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/employee-management.vue'),
      },
      {
        path: '/employee/statistics',
        name: '员工信息',
        meta: {
          title: '员工信息',
          icon: () => h(InformationCircleIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/employee-statistics.vue'),
      },
    ],
  },
  {
    path: '/department',
    component: MainLayout,
    meta: {
      roles: ['admin'],
    },
    children: [
      {
        path: '/department/management',
        name: '部门管理',
        meta: {
          title: '部门管理',
          icon: () => h(BuildingOffice2Icon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/department-management.vue'),
      },
    ],
  },
  {
    path: '/class',
    component: MainLayout,
    meta: {
      roles: ['admin'],
    },
    children: [
      {
        path: '/management',
        name: '班级管理',
        meta: {
          title: '班级管理',
          icon: () => h(BuildingLibraryIcon, { class: 'w-4 h-4' }),
        },
        component: () => import('@/views/class-management.vue'),
      },
    ],
  },
]

export const router = createRouter({
  history: createWebHashHistory(),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

// reset router
export function resetRouter() {
  router.getRoutes().forEach((route) => {
    const { name, meta } = route
    if (name && (meta.roles?.length || 0) > 0) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
  router.replace('/')
}

export function setupRouter(app: App) {
  app.use(router)

  setupRouterGuard(router)
}
