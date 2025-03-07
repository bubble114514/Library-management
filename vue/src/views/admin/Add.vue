<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">新增用户</h2>
    <el-form :model="form" status-icon :rules="rules" ref="ruleForm" style="width: 80%" label-width="120px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="年龄" prop="age">
        <el-input v-model.number="form.age" placeholder="请输入年龄" type="number"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio v-model="form.sex" label="男">男</el-radio>
        <el-radio v-model="form.sex" label="女">女</el-radio>
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系方式"></el-input>
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入地址"></el-input>
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
  name: 'AddUser',
  data() {
    const checkAge = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('年龄不能为空'));
      }
      if (!/^[0-9]+$/.test(value)) {
        callback(new Error('请输入数字值'));
      }
      if (parseInt(value) < 0 || parseInt(value) > 120) {
        callback(new Error('年龄必须在0到120之间'));
      }
      callback();
    };
    const checkPhone = (rule, value, callback) => {
      if(!/^[1][3,4,5,6,7,8,9][0-9]{9}$/.test(value)){
        callback(new Error('请正确输入手机号'));
      }
      callback();
    };

    return {
      form: {
        name: '',
        age: null, // 修改 age 的初始值为 null
        sex: '',
        phone: '',
        address: '',
      },
      rules: {
        // 表单验证规则
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'},
        ],
        age: [
          {validator: checkAge, trigger: 'blur'}
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
          request.post('user/save', this.form).then((res) => {
            if (res.code === '200') {
              // 提交表单数据
              this.$notify.success('新增成功');
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
   