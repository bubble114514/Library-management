<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">新增借书记录</h2>
    <el-form :model="form" status-icon :rules="rules" ref="ruleForm" style="margin-left: 50px" label-width="120px">
      <el-form-item label="图书标准码" prop="bookNo">
        <el-select v-model="form.bookNo" filterable placeholder="请选择" @change="selBook">
          <el-option
              filterable
              v-for="item in books"
              :key="item.id"
              :label="item.bookNo"
              :value="item.bookNo">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="图书名称" prop="bookName">
        <el-input v-model="form.bookName" disabled placeholder="请输入图书名称"></el-input>
      </el-form-item>
      <el-form-item label="所需积分" prop="score">
        <el-input disabled v-model="form.score" placeholder="请输入所需积分"></el-input>
      </el-form-item>
      <el-form-item label="剩余数量" prop="nums">
        <el-input disabled v-model="form.nums"></el-input>
      </el-form-item>
      <el-form-item label="会员码" prop="userId">
        <el-select v-model="form.userId" filterable placeholder="请选择" @change="selUser">
          <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.username"
              :value="item.username">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="用户名称" prop="userName">
        <el-input disabled v-model="form.userName" placeholder="请输入用户名称"></el-input>
      </el-form-item>
      <el-form-item label="用户联系方式" prop="userPhone">
        <el-input disabled v-model="form.userPhone" placeholder="请输入手机号"></el-input>
      </el-form-item>


      <el-form-item label="账户积分" prop="account">
        <el-input disabled v-model="form.account"></el-input>
      </el-form-item>
      <el-form-item label="借出天数" prop="days">
        <el-input-number v-model="form.days" :min="1" :max="30"></el-input-number>
      </el-form-item>

    </el-form>
    <div style="text-align: center;margin-top: 40px">
      <el-button type="primary" @click="save" size="medium">提交</el-button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: 'AddBook',
  data() {
    return {
      books: [],
      users: [],
      form: {
        bookNo: '',
        bookName: '',
        score: 0,
        nums: '',
        userId: '',
        userName: '',
        userPhone: '',
        account: '',
        days: '1',
      },
      rules: {
        bookNo: [
          {required: true, message: '请选择图书标准码', trigger: 'change'},
        ],
        userId: [
          {required: true, message: '请选择会员码', trigger: 'change'},
        ],
      },
    }
  },
  created() {
    request.get('/book/list').then((res) => {
      this.books = res.data
    })
    request.get('/user/list').then((res) => {
      this.users = res.data
    })
  },
  watch: {
    'form.days': function (newVal) {
      if (this.form.bookNo) {
        const book = this.books.find(v => v.bookNo === this.form.bookNo);
        if (book) {
          this.form.score = book.score * newVal;
        }
      }
    }
  },
  methods: {
    save() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          request.post('borrow/save', this.form).then((res) => {
            if (res.code === '200') {
              // 提交表单数据
              this.$notify.success('新增成功');
              this.$refs['ruleForm'].resetFields(); // 重置表单
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    },
    selBook() {
      const book = this.books.find(v => v.bookNo === this.form.bookNo)
      request.get('/book/' + book.id).then((res) => {
        this.$set(this.form, 'bookName', res.data.name)
        this.form.score = res.data.score * this.form.days
        this.form.nums = res.data.nums

      })

    },
    selUser() {
      const user = this.users.find(v => v.username === this.form.userId)
      if (user) {
        request.get('/user/' + user.id).then((res) => {

          if (res.data.status === false) {
            this.$notify.error('该用户已被冻结，请联系管理员');
            // 清空用户相关字段
            this.form.userId = '';
            this.form.userName = '';
            this.form.userPhone = '';
            this.form.account = '';
          } else {
            this.$set(this.form, 'userName', res.data.name)
            this.form.userPhone = res.data.phone
            this.form.account = res.data.account
          }


        })
      }

    },

  }
}
</script>
