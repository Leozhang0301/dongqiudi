<template>
	<div class="inner_main">
		<el-table :data="report_data" border style="width: 100%;height: 90%;" max-height="90%"
					:cell-style="{padding:'8px 8px'}" cell-class-name="words-ellipsis">
		    <el-table-column prop="ID" label="ID" header-align="center" align="center"></el-table-column>
		    <el-table-column prop="新闻标题" label="新闻标题"  width="400" header-align="center"></el-table-column>
		    <el-table-column prop="评论内容" label="评论内容" header-align="center" align="center"></el-table-column>
		    <el-table-column prop="用户名" label="用户名" header-align="center" align="center" :formatter="tellGender"></el-table-column>
		    <el-table-column prop="评论时间" label="评论时间"  header-align="center" align="center"></el-table-column>
		    <el-table-column label="操作" header-align="center" align="center" width="300">
		      <template slot-scope="scope">
		        <el-button type="danger" size="small" @click="deleteComment(scope)">删除</el-button>
				<el-button type="info" size="small"   @click="cancelComment(scope)">取消</el-button>
		      </template>
		    </el-table-column>
		</el-table>
		<div class="vertical_center" style="height: 10%;justify-content: flex-end;">
			<el-pagination  style="margin-right: 50px;" background layout="prev, pager, next,jumper" @current-change="changePage" :page-size="10" :total="100"></el-pagination>
		</div>
	</div>
</template>

<script>
	export default{
		name:'report',
		created() {
			this.$axios.get("http://8.129.27.254:8000/getreports").then((res)=>{
				this.report_data=res.data
			})
		},
		data(){
			return{
				report_data:[
					
				]
			}
		},
		methods:{
			deleteComment(scope){
				this.$axios.get("http://8.129.27.254:8000/deletecomment",{params:{id:scope.row.ID}}).then((res)=>{
					this.$message({
					  message: '删除成功',
					  type: 'warning',
					  duration:"1500"
					});
					location.reload()
				}).catch((err)=>{
					console.log("接口错误",err)
				});
				console.log(scope.row.ID);
			},
			cancelComment(scope){
				this.$axios.get("http://8.129.27.254:8000/cancelreport",{params:{id:scope.row.ID}}).then((res)=>{
					this.$message({
					  message: '删除成功',
					  type: 'warning',
					  duration:"1500"
					});
					location.reload()
				}).catch((err)=>{
					console.log("接口错误",err)
				});
				console.log(scope.row.ID);
			}
		}
	}
</script>

<style>
	.inner_main{
		width: 100%;
		height: 84%;
		border-radius: 10px;
		background-color: #FFFFFF;
		border: solid 1px #EEEFF1;
	},
	.vertical_center{
		display: flex;
		align-items: center;
	}
</style>
