<template>
  <div>
    <div>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入图书名称" v-model="params.name"></el-input>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入图书标准码" v-model="params.bookNo"></el-input>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入用户名称" v-model="params.userName"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="load"><i class="el-icon-search"></i>搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
    </div>

    <div>
      <el-table :data="tableData" stripe v-if="dataLoaded" row-key="id" default-expand-all>
        <el-table-column prop="id" label="编号" width="50" align="center"></el-table-column>
        <el-table-column prop="bookName" label="图书名称" align="center" ></el-table-column>
        <el-table-column prop="bookNo" label="图书标准码" align="center" width="100px"></el-table-column>
        <el-table-column prop="userId" label="会员码" align="center" width="100px"></el-table-column>
        <el-table-column prop="userName" label="用户名称" width="80px" align="center"></el-table-column>
        <el-table-column prop="userPhone" label="用户联系方式" width="110px" align="center"></el-table-column>
        <el-table-column prop="score" label="所用积分" width="80px" align="center"></el-table-column>
        <el-table-column prop="status" label="借出状态" width="80px" align="center"></el-table-column>
        <el-table-column prop="days" label="借出天数" width="80px" align="center" ></el-table-column>
        <el-table-column prop="createtime" label="借书日期" width="100px" align="center"></el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="100px" align="center"></el-table-column>
        <el-table-column prop="note" label="到期提醒" width="100px" align="center">
          <template v-slot="scope">
            <el-tag type="success" v-if="scope.row.note === '正常'"> {{scope.row.note}}</el-tag>
            <el-tag type="primary" v-if="scope.row.note === '即将到期'">{{scope.row.note}}</el-tag>
            <el-tag type="warning" v-if="scope.row.note === '已到期'">{{scope.row.note}}</el-tag>
            <el-tag type="danger" v-if="scope.row.note === '已超期'">{{scope.row.note}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="管理" width="80" align="center" >
          <template v-slot="scope">
            <el-button type="primary" @click="returnBooks(scope.row)">还书</el-button>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="80" align="center">
          <template v-slot="scope">
            <!-- scope.row就是当前行数据 -->
<!--            <el-button type="primary" @click="$router.push('/editBorrow?id=' + scope.row.id)">编辑</el-button>-->
            <el-popconfirm
                title="确定删除吗？"
                @confirm="del(scope.row.id)"
                style="margin-left: 5px"
            >
              <el-button type="danger" slot="reference">删除</el-button>
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
import Cookies from "js-cookie";

export default {
  name: 'BorrowList',

  data() {
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      tableData: [],
      total: 0,
      params: {
        name: '',
        bookNo: '',
        pageNum: 1,
        pageSize: 10,
      },
      searchName: '',
      searchBookNo:'',
      dataLoaded: false,

    };
  },
  created() {
    this.load();
  },
  methods: {

    load() {
      request.get('/borrow/page', {
        params: this.params
      }).then(res => {
        if (res && res.code === '200') {
          this.tableData = res.data.list;
          this.total = res.data.total;
          this.dataLoaded = true;
        }
      }).catch(error => {
        console.error('加载数据出错：', error);
        this.dataLoaded = false;
      });
    },

    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    reset() {
      this.params = {
        bookName: '',
        bookNo: '',
        userName: '',
        pageNum: 1,
        pageSize: 10
      };
      this.searchName = '';
      this.searchBookNo = '';
      this.load();
    },
    del(id) {
      request.delete("/borrow/delete/" + id).then((res) => {
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
    },
    returnBooks(row){

    }


  }
};
</script>