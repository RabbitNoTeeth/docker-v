import Vue from 'vue'
import axios from 'axios'
import qs from 'qs'

const baseUrl = process.env.API;

const axiosConfig = {
  withCredentials: true,
  transformRequest: [(data) => qs.stringify(data, {skipNulls: true})]
}

const instance = axios.create({
  baseURL: baseUrl,
  ...axiosConfig
})
instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';


Vue.prototype.$axios = instance
Vue.prototype.$baseUrl = baseUrl

