import axios from "axios";
import {ElLoading, ElMessage} from "element-plus";
import type {LoadingInstance} from "element-plus/lib/components/loading/src/loading";
import router from "@/router";

let loadingInstance: LoadingInstance;
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 60000
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    try {
        loadingInstance = ElLoading.service({fullscreen: true, lock: true,});
        return config;
    } catch (error) {
        return Promise.reject(error);
    }
}, function (error) {
    // 对请求错误做些什么
    ElMessage.error('网络错误')
    loadingInstance?.close();
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    try {
        if (response.status === 200) {
            // const data = response.data;
            return response;
        } else {
            return Promise.reject();
        }
    } finally {
        loadingInstance?.close();
    }
}, function (error) {
    loadingInstance?.close();
    ElMessage.error('网络错误')
    return Promise.reject(error);
});
const req = axios
export default req