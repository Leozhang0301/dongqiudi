<template>
	<div class="inner_main">
		<el-table :data="news_data" border style="width: 100%;height: 90%;" max-height="90%"
					:cell-style="{padding:'8px 8px'}" cell-class-name="words-ellipsis" stripe>
		    <el-table-column prop="ID" label="ID" header-align="center" align="center"></el-table-column>
		    <el-table-column prop="标题" label="标题"  width="600" header-align="center"></el-table-column>
		    <el-table-column prop="时间" label="发布日期" header-align="center" align="center"></el-table-column>
		    <el-table-column label="操作" header-align="center" align="center">
		      <template slot-scope="scope">
		        <el-button type="danger" size="small" @click="deleteData(scope)">删除</el-button>
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
		created() {
			this.$axios.get("http://8.129.27.254:8000/getnews").then((res)=>{
				this.news_data=res.data
			})
		},
		name:'news',
		data(){
			return{
				news_data:[
					
				]
			}
		},
		methods:{
			deleteData(scope){
				this.$axios.get("http://8.129.27.254:8000/deletenews",{params:{id:scope.row.ID}}).then((res)=>{
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
			changePage(new_val)
			{
				console.log(new_val);
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
