<template>
  <div>
    <div>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入名称" v-model="searchName"></el-input>
      <el-input style="width: 300px;margin-left:5px" placeholder="请输入联系方式" v-model="searchPhone"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="search"><i class="el-icon-search"></i>搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
    </div>
    <div>
      <el-table  :data="tableData" stripe v-if="dataLoaded">
        <el-table-column prop="id" label="编号" width="50"></el-table-column>
        <el-table-column prop="username" label="会员卡号" width="150"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="age" label="年龄" width="50"></el-table-column>
        <el-table-column prop="address" label="地址" width="100"></el-table-column>
        <el-table-column prop="phone" label="联系方式" width="100"></el-table-column>
        <el-table-column prop="sex" label="性别" width="80" position="center"></el-table-column>
        <el-table-column prop="account" label="账户积分" width="80"></el-table-column>
        <el-table-column label="状态" width="80" position="center">
          <template v-slot="scope">
            <el-switch
                v-model="scope.row.status"
                @change="changeStatus(scope.row)"
                active-color="#13ce66"
                inactive-color="#ff4949">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createtime" label="创建时间" width="150"></el-table-column>
        <el-table-column prop="updatetime" label="更新时间" width="150"></el-table-column>


        <el-table-column label="操作">
          <template v-slot="scope">
            <!-- scope.row就是当前行数据 -->
            <el-button type="warning" @click="handleAccountAdd(scope.row)">充值</el-button>
            <el-button type="primary" @click="$router.push('/editUser?id=' + scope.row.id)">编辑</el-button>

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
    <el-dialog title="充值" :visible.sync="dialogFormVisible" width="35%">
      <el-form :model="form" label-width="100px" ref="addForm" :rules="rules">
        <el-form-item label="当前账户积分" prop="account">
          <el-input v-model="form.account" disabled autocomplete:="off"></el-input>
        </el-form-item>
        <el-form-item label="充值积分" prop="score">
          <el-input v-model="form.score" autocomplete:="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addAccount">确 定</el-button>
        <el-button @click="dialogFormVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'User',
  data() {
    const checkNums = (rule, value, callback) => {
      value = parseInt(value)
      if (!Number.isInteger(value) || value <= 0) {
        return callback(new Error('请输入大于零的整数'));
      } else if (value > 1000) {
        return callback(new Error('请输入小于1k的整数数'));
      }
      callback();
    };
    return {
      tableData: [],
      total: 0,
      params: {
        id: '',
        username: '',
        name: '',
        age: '',
        sex: '',
        address: '',
        phone: '',
        pageNum: 1,
        pageSize: 10
      },
      dialogFormVisible: false,
      form: {},
      searchName: '',
      searchPhone: '',
      dataLoaded: false,
      rules: {
        score: [
          {required: true, message: '请输入积分', trigger: 'blur'},
          {validator: checkNums, trigger: 'blur'},
        ]
      }
    };
  },
  created() {
    this.load();
  },
  methods: {
    changeStatus(row) {
      request.put('/user/update', row).then((res) => {
        if (res.code === '200') {
          this.$notify.success('操作成功')
          this.load()
        } else {
          this.$notify.error(res.msg);
        }
      });
    },
    load() {
      console.log('请求参数:', this.params); // 添加日志输出
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
    },
    handleAccountAdd(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    addAccount() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          request.post("/user/account", this.form).then((res) => {
            if (res.code === '200') {
              this.$notify.success("充值成功")
              this.dialogFormVisible = false
              this.load()
            } else {
              this.$notify.error(res.msg)
            }
          })
        }
      });
    },
  }
};
</script>

