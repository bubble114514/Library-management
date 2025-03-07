<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">编辑借书信息</h2>
    <el-form :model="form" :inline="true" status-icon :rules="rules" ref="ruleForm" style="margin-left: 50px"
             label-width="120px">
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

      <el-form-item label="剩余数量" prop="nums">
        <el-input disabled v-model="form.nums"></el-input>
      </el-form-item>

      <el-form-item label="用户账户积分" prop="account">
        <el-input disabled v-model="form.account" ></el-input>
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
  name: 'EditBook',
  data() {
    return {
      books: [],
      users: [],
      categories: [],
      form: {},
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

    const id = this.$route.query.id
    request.get('/borrow/' + id).then((res) => {
      this.form = res.data
    })


  },

  methods: {
    save() {
      request.put('/borrow/update', this.form).then((res) => {
        if (res.code === '200') {
          this.$notify.success('更新成功')
          this.$router.push('/borrowList')
        } else {
          this.$notify.error(res.msg);
        }
      });
    },
    selBook() {
      const book = this.books.find(v => v.bookNo === this.form.bookNo)
      request.get('/book/' + book.id).then((res) => {
        if (book) {
          this.form.bookName = res.data.name
          this.form.score = res.data.score
          this.form.nums = res.data.nums
        }
      })

    },
    selUser() {
      const user = this.users.find(v => v.username === this.form.userId)
      request.get('/user/' + user.id).then((res) => {
        if (user) {
          this.form.userName = res.data.name
          this.form.userPhone = res.data.phone
          this.form.account = res.data.account
        }
      })

    }
  }
}
</script>
