<template>
  <div class="item">
    <div>用户名：{{ admin.username }}</div>
    <div>手机号：{{ admin.phone }}</div>
    <div>邮箱：{{ admin.email }}</div>
  </div>


</template>

<script>

import Cookies from "js-cookie";
import request from "@/utils/request";
import * as echarts from "echarts"
export default {
  name: "HomeView",
  data() {
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {}
    }
  },
  created() {
    request.get('/admin/' + this.admin.id).then((res) => {
      this.admin =res.data
    })
  }
}
</script>


<style>
.item{
  margin: 10px 0;
  color: #666;
}
</style>