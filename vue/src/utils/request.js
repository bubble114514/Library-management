import axios from "axios";
import router from "@/router";
import Cookies from "js-cookie";

//使用axios库发送HTTP请求
const request = axios.create({
    baseURL: 'http://localhost:9090/api',
    timeout: 5000,
});


// request 拦截器
// 拦截请求，设置请求头，设置token
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

       const adminJSON = Cookies.get('admin');
   if (adminJSON) {
       const admin = JSON.parse(adminJSON);
       config.headers['token'] = admin.token; // 设置请求头
   }
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
//可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        if (res.code==='401'){
            router.push('/login');
        }
        return res;
    },
    error => {
        console.log('err' + error); // for debug
        return Promise.reject(error);
    }
);

export default request;
