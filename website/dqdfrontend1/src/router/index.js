import Vue from 'vue';
import VueRouter from 'vue-router';
import login from '../pages/login.vue';
Vue.use(VueRouter)

const route = [
  {
    path: '/',
    name: 'login',
    component: login,
  },
  // {
  //   path: '/login',
  //   name: 'login',
  //   component: login,
  // },
  // {
	 //  path:"/home",
	 //  component:Home,
	 //  children:[
		//   {
		// 	  path:"table",
		// 	  component:table
		//   },
		//   {
		//   			  path:"table",
		//   			  component:table
		//   },
	 //  ]
  // }
]

const router = new VueRouter({
  routes:route
})

export default router
