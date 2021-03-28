import Vue from 'vue';
import VueRouter from 'vue-router';
import login from '../pages/login.vue';
import home from '../pages/home.vue'
import news from '../components/news.vue'
import report from '../components/report.vue'
Vue.use(VueRouter)

const route = [
  {
    path: '/',
    name: 'login',
    component: login,
  },
  {
    path: '/login',
    name: 'login2',
    component: login,
  },
  {
	  path:'/home',
	  name:'home',
	  component:home,
	  children:[
		  {
			  path:'news',
			  alias:'',
			  component:news,
			  name:'news'
		  },
		  {
			  path:'report',
			  component:report,
			  name:'report'
		  }
	  ]
  }
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
