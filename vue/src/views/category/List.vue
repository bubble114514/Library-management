<template>
  <div>
    <div>
      <el-input style="width: 240px;margin-bottom: 15px" placeholder="请输入名称" v-model="searchName"></el-input>
      <el-input style="width: 300px;margin-left:5px" placeholder="请输入联系方式" v-model="searchPhone"></el-input>
      <el-input style="width: 300px;margin-left:5px" placeholder="请输入邮箱" v-model="searchEmail"></el-input>
      <el-button style="margin-left: 5px" type="primary" @click="search"><i class="el-icon-search"></i>搜索</el-button>
      <el-button style="margin-left: 5px" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
    </div>
    <div>
      <el-table :data="tableData" stripe v-if="dataLoaded">
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="phone" label="联系方式"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="createtime" label="创建时间"></el-table-column>
        <el-table-column prop="updatetime" label="更新时间"></el-table-column>

        <el-table-column label="状态">
          <template v-slot="scope">
            <el-switch
              v-model="scope.row.status"
              @change="changeStatus(scope.row)"
              active-color="#13ce66"
              inactive-color="#ff4949">
            </el-switch>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="230">
          <template v-slot="scope">
            <!-- scope.row就是当前行数据 -->
            <el-button type="primary" @click="$router.push('/editAdmin?id=' + scope.row.id)">编辑</el-button>
            <el-popconfirm
                title="确定删除吗？"
                @confirm="del(scope.row.id)"
                style="margin-left: 5px"
            >
              <el-button type="danger" slot="reference">删除</el-button>
            </el-popconfirm>

            <el-button style="margin-left: 5px" type="warning" @click="handleChangePass(scope.row)">修改密码</el-button>

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

    <el-dialog title="修改密码" :visible.sync="dialogFormVisible" width="30%">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="旧密码" prop="oldPass">
          <el-input v-model="form.oldPass" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPass">
          <el-input v-model="form.newPass" autocomplete="off" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePass">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
import Cookies from "js-cookie";

export default {
  name: 'User',
  data() {
    return {
      tableData: [],
      total: 0,
      dialogFormVisible: false,
      form: {},
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      params: {
        username: '',
        phone: '',
        email: '',
        pageNum: 1,
        pageSize: 10,
      },
      searchName: '',
      searchPhone: '',
      searchEmail: '',
      dataLoaded: false,
      rules: {
        newPass: [
          {required: true, message: '请输入新密码', trigger: 'blur'},
          {min: 3, message: '密码长度不能小于3位', trigger: 'blur'},
        ],
        oldPass: [
          {required: true, message: '请输入旧密码', trigger: 'blur'},
          {min: 3, message: '密码长度不能小于3位', trigger: 'blur'},
        ],
      },
    };
  },
  created() {
    this.load();
  },
  methods: {
    changeStatus(row) {
      if (this.admin.id === row.id){
        this.$notify.warning('不能修改自己的状态');
        row.status = !row.status;
        return;
      }
      request.put('/admin/update', row).then((res) => {
        if (res.code === '200') {
          this.$notify.success('操作成功')
          this.load()
        } else {
          this.$notify.error(res.msg);
        }
      });
    },
    load() {
      request.get('/admin/page', {
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
    search() {
      this.params.username = this.searchName;
      this.params.phone = this.searchPhone;
      this.params.email = this.searchEmail;
      this.params.pageNum = 1;
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    reset() {
      this.params = {
        username: '',
        phone: '',
        email: '',
        pageNum: 1,
        pageSize: 10
      };
      this.searchName = '';
      this.searchPhone = '';
      this.searchEmail = '';

      this.load();
    },
    del(id) {
      request.delete("/admin/delete/" + id).then((res) => {
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
    handleChangePass(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.dialogFormVisible = true;
    },
    savePass() {
      this.$refs['formRef'].validate(valid => {
        if (valid) {
          const data = {
            username: this.form.username,
            password: this.form.oldPass, // 旧密码
            newPass: this.form.newPass   // 新密码
          };
          request.put('/admin/password', data).then((res) => {
            if (res.code === '200') {
              this.$notify.success("修改成功");
              // 如果当前修改用户的id等于当前登录用户的用id，则重新登录
              if (this.form.id === this.admin.id) {
                Cookies.remove('admin');
                this.$router.push('/login');
              }
              this.dialogFormVisible = false;
            } else {
              this.load()
              this.$notify.error("旧密码错误！");
            }
          }).catch(error => {
            this.$notify.error("修改失败，请重试");
            console.error('Update password failed:', error);
          });
        }
      });
    },

  }
};
</script>