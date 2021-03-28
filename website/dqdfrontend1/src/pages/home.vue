<template>
	<el-container  style="width: 100%;height: 100%;">
		<!-- 头部栏 -->
	  <el-header class="header" style="border-bottom: #EEEFF1 1px solid;display: flex;justify-content: center;align-items: center;">
		  <el-row style="width: 100%;">
			<el-col :span="20">
				<div style="display: flex;align-items: center;">
					<div style="text-align: left;letter-spacing: 5px;">DQD后台管理系统</div>
				</div>
			</el-col>
		  </el-row>
	  </el-header>
	  <!-- 中底部 -->
	  <el-container style="width: 100%;height: 100%;">
		  <!-- 侧边栏 -->
	    <el-row>
			  <el-col :span="12">
			    <el-menu
			      default-active="/home/news"
			      class="el-menu-vertical-demo"
			      @open="handleOpen"
			      @close="handleClose"
				  style="width: 300px;"
				  :router="true">
			      <el-menu-item index="/home/news">
			        <i class="el-icon-menu"></i>
			        <span slot="title">新闻管理</span>
			      </el-menu-item>
			      <el-menu-item index="/home/report">
			        <i class="el-icon-document"></i>
			        <span slot="title">举报管理</span>
			      </el-menu-item>
			    </el-menu>
			  </el-col>
		</el-row>
		<!-- 主体部分 -->
	    <el-main style="background-color: #F8F9FE;height: 100%;overflow-x: hidden;">
			<router-view style="width: 100%;height: 100%;" name="default"></router-view>
		</el-main>
	  </el-container>
	</el-container>
</template>

<script>
	export default{
		name:"home",
		methods: {
		  handleOpen(key, keyPath) {
			console.log(key, keyPath);
		  },
		  handleClose(key, keyPath) {
			console.log(key, keyPath);
		  }
		}
	}
</script>

<style>
	.header{
		font-size: 25px;
	}
</style>
