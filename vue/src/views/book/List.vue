<template>
  <div>
    <div>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入图书名称" v-model="params.name"></el-input>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入图书标准码" v-model="params.bookNo"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="load"><i class="el-icon-search"></i>搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
    </div>

    <div>
      <el-table :data="tableData" stripe v-if="dataLoaded" row-key="id" default-expand-all>
        <el-table-column prop="id" label="编号" width="80" class-name="fixed-height" align="center"></el-table-column>
        <el-table-column prop="name" label="图书名称" class-name="fixed-height" width="120" ></el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip width="100" class-name="fixed-height"></el-table-column>
        <el-table-column prop="publishDate" label="出版日期" class-name="fixed-height"  width="100px"></el-table-column>
        <el-table-column prop="author" label="作者" class-name="fixed-height" align="center"></el-table-column>
        <el-table-column prop="publisher" label="出版社" class-name="fixed-height"></el-table-column>
        <el-table-column prop="category" label="分类" class-name="fixed-height" ></el-table-column>
        <el-table-column prop="bookNo" label="图书标准码" class-name="fixed-height" align="center" width="100px"></el-table-column>
        <el-table-column prop="score" label="积分" class-name="fixed-height" align="center" width="50px"></el-table-column>
        <el-table-column prop="nums" label="数量" class-name="fixed-height" align="center" width="50px"></el-table-column>
        <el-table-column prop="cover" label="封面" width="100" class-name="fixed-height" align="center">
          <template v-slot="scope">
            <el-image :src="scope.row.cover" :preview-src-list="[scope.row.cover]" style="width: 80px;height: 80px"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="createtime" label="创建时间" class-name="fixed-height"></el-table-column>
        <el-table-column prop="updatetime" label="更新时间" class-name="fixed-height"></el-table-column>

        <el-table-column label="操作" width="150" class-name="fixed-height">
          <template v-slot="scope">
            <!-- scope.row就是当前行数据 -->
            <el-button type="primary" @click="$router.push('/editBook?id=' + scope.row.id)">编辑</el-button>
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
  name: 'BookList',

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

      rules: {
        name: [
          {required: true, message: '请输入图书名称', trigger: 'blur'},
        ],

      },
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      request.get('/book/page', {
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
        name: '',
        bookNo: '',
        pageNum: 1,
        pageSize: 10
      };
      this.searchName = '';
      this.searchBookNo = '';
      this.load();
    },
    del(id) {
      request.delete("/book/delete/" + id).then((res) => {
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
  }
};
</script>

<style scoped>
.fixed-height {
  height: 40px; /* 设置固定高度 */
  overflow: hidden; /* 隐藏溢出内容 */
  white-space: nowrap; /* 防止内容换行 */
  text-overflow: ellipsis; /* 显示省略号 */
}
</style>
