<template>
	<div class="middle">
		<!-- 欢迎登录 -->
		<div style="text-align: center;">
			<img src="../assets/welcome.png" style="width: 153px;height: 32px;margin-top: 50px; margin-right: 200px;"/>
		</div>
		<!-- 输入账号 -->
		<div class="login_input_outer" style="margin-top: 80px;">
			<i class="el-icon-user login_icon" ></i>
			<el-input ref='account' class="login_input_inner" v-model="account" placeholder="请输入您的用户名"
						@keyup.enter.native="focusPassword" type="text" v-on:keyup.enter="submit"></el-input>
		</div>
		<!-- 输入密码 -->
		<div class="login_input_outer" style="margin-top: 30px;">
			<i  class="el-icon-lock login_icon" ></i>
			<el-input ref='password' class="login_input_inner" v-model="password" placeholder="请输入密码" type="password"
						@keyup.enter.native="login"></el-input>
		</div>
		<!-- 记住密码 -->
		<!-- <div class="login_input_outer" style="margin-top: 30px;border: unset;justify-content: flex-end;height: 20px;">
			<el-checkbox v-model="remember_password" style="margin-top: -3px;"></el-checkbox>
			<img style="width: 72px;height: 16px;margin-left: 5px;" src="../assets/pic/login/remember.svg" />
		</div> -->
		<!-- 登录按钮 -->
		<div @click="login" class="login_input_outer" style="cursor: pointer; margin-top: 30px;border: unset;background-color: #FFAB6B;height: 50px;border-radius: 5px;justify-content: center;">
			<img style="width: 114px;height: 30px;" src="../assets/login_button.svg" />
		</div>
	</div>
</template>

<script>
	export default{
		name:"login",
		data(){
			return {
				// 账号
				account:"",
				// 密码
				password:""
			}
		},
		methods:{
			// 密码输入框获取焦点
			focusPassword(){
				this.$refs.password.focus();
			},
			// 登录
			login(){
				// 如果账号密码不符合规范
				if(this.account===""||this.password===""||this.account.includes(" ")||this.password.includes(" "))
				{
					this.$message({
					  message: '请输入完整的账号和密码!',
					  type: 'warning',
					  duration:"1500"
					});
					return false;
				}
				// this.$axios.post("login/Login.php",{account:this.account,password:this.$md5(this.password)}).then((res)=>{
				// 	// 如果账号密码正确
				// 	console.log(res);
				// 	// 存在且密码正确
				// 	if(res.status==1)
				// 	{
				// 		this.$router.push("/home")
				// 	}
				// }).catch((err)=>{
				// 	console.log("登录接口报错",err);
				// })
				this.$axios.get("http://8.129.27.254:8000/checkmanageruser",{params:{username:this.account,pwd:this.password}}).then((res)=>{
					console.log(res);
					if(res.data==1){
						//媒体账户
						window.location.href="http://8.129.27.254/publish/editor.html"
					}else if(res.data==0){
						//管理员
						this.$router.push("/home")
					}else{
						//普通用户
						this.$message({
						  message: '没有权限进入后台',
						  type: 'warning',
						  duration:"1500"
						});
					}
				}).catch((err)=>{
					console.log("接口错误",err)
				})
			}
		}
	}
</script>

<style>
	.middle{
		position: absolute;
		top: 50%;
		left: 50%;
		width: 600px;
		height: 500px;
		margin-top: -250px;
		margin-left: -200px;
		display: block; 
	}
	.login_icon{
		font-size: 28px;
		color: #EAEAEA;
	}
	.login_input_outer{
		width: 60%;
		border-bottom: #EAEAEA solid 1px;
		display: flex;
		align-items: center;
	}
	.login_input_inner{
		border: unset;
		width: 400px;
	}
</style>
