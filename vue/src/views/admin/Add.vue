<template>
<!--  新增-->
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">新增管理员</h2>
    <el-form :model="form" status-icon :rules="rules" ref="ruleForm" style="width: 80%" label-width="120px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
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
  name: 'AddAdmin',
  data() {
    const checkPhone = (rule, value, callback) => {
      if(!/^[1][3-9][0-9]{9}$/.test(value)){
        callback(new Error('请正确输入手机号'));
      }
      callback();
    };
    return {
      form: {},
      rules: {
        // 表单验证规则
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
        ],
        phone: [
          {validator: checkPhone, trigger: 'blur'}
        ],
      },
    }
  },
  methods: {
    save() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          request.post('admin/save', this.form).then((res) => {
            if (res.code === '200') {
              // 提交表单数据
              this.$notify.success('新增成功，默认密码为 123');
              this.$refs['ruleForm'].resetFields();//重置表单
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    }
  }
}
</script>
   