<template>
  <div>
    <div>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入名称" v-model="searchName"></el-input>
      <el-input style="width: 300px;margin-left:5px" placeholder="请输入联系方式" v-model="searchPhone"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="search"><i class="el-icon-search"></i>搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
    </div>
    <div>
      <el-table :data="tableData" stripe v-if="dataLoaded">
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="username" label="会员卡号"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="age" label="年龄"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="phone" label="联系方式"></el-table-column>
        <el-table-column prop="sex" label="性别"></el-table-column>
        <el-table-column prop="createtime" label="创建时间"></el-table-column>
        <el-table-column prop="updatetime" label="更新时间"></el-table-column>


        <el-table-column label="操作">
          <template v-slot="scope">
            <!-- scope.row就是当前行数据 -->
            <el-button type="primary" @click="$router.push('/editUser?id=' + scope.row.id)">编辑</el-button>
            <el-popconfirm
                title="确定删除吗？"
                @confirm="del(scope.row.id)"
                style="margin-left: 5px"
            >
              <el-button type="danger" slot="reference" >删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div v-else>加载中...</div>
    </div>
    <!--分页-->
    <div style="margin-top: 20px">
      <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'HomeView',
  data() {
    return {
      tableData: [],
      total: 0,
      params: {
        name: '',
        phone: '',
        pageNum: 1,
        pageSize: 10
      },
      searchName: '',
      searchPhone: '',
      dataLoaded: false
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      request.get('/user/page', {
        params: this.params
      }).then(res => {
        if (res && res.code === '200') {
          this.tableData = res.data.list || [];
          this.total = res.data.total || 0;
          this.dataLoaded = true;
        }
      }).catch(error => {
        console.error('加载数据出错：', error);
        this.dataLoaded = false;
      });
    },
    search() {
      this.params.name = this.searchName;
      this.params.phone = this.searchPhone;
      this.params.pageNum = 1;
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    reset() {
      this.params = {
        name: '',
        phone: '',
        pageNum: 1,
        pageSize: 10
      };
      this.searchName = '';
      this.searchPhone = '';
      this.load();
    },
    del(id) {
      request.delete("/user/delete/" + id).then((res) => {
        if (res.code === '200') {
          this.$notify.success("删除成功");
          this.load(); // 重新加载数据
        } else {
          this.$notify.error(res.msg);
        }
      }).catch(error => {
        this.$notify.error("删除失败，请重试");
        console.error('Delete failed:', error);
      });
    }
  }
};
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>
