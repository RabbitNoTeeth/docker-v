<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card>
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">new session - ssh</div>
        <q-space/>
        <q-btn icon="close" flat round dense v-close-popup @click="close"/>
      </q-card-section>
      <q-card-section>
        <q-form
          @submit="onSubmit"
          @reset="onReset"
          class="q-gutter-md"
          style="width: 500px"
        >
          <q-input
            filled
            v-model="formData_.name"
            label="name *"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'please define a name for the ssh session']"
          />
          <q-input
            filled
            v-model="formData_.host"
            label="host *"
            :rules="[ val => val && val.length > 0 || 'please input the ssh host']"
          />
          <q-input
            filled
            v-model="formData_.port"
            label="port *"
            :rules="[ val => val !== null && val !== '' || 'please input the ssh port']"
          />
          <q-input
            filled
            v-model="formData_.user"
            label="user *"
            :rules="[ val => val && val.length > 0 || 'please input the ssh user']"
          />
          <q-input
            filled
            v-model="formData_.password"
            label="password *"
            :rules="[ val => val && val.length > 0 || 'please input the ssh password']"
          />
          <div style="text-align: right">
            <q-btn label="submit" type="submit" color="primary"/>
            <q-btn label="reset" type="reset" color="primary" flat class="q-ml-sm"/>
          </div>
        </q-form>
      </q-card-section>
      <q-inner-loading :showing="loading">
        <div class="row">
          <q-spinner-gears size="50px" color="primary" />
          <span style="margin-left: 15px;font-size: 26px;color: #1976d2">connecting...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "SessionAddForm",
  data() {
    return {
      show: true,
      formData_: {
        name: '192.168.0.105',
        host: '192.168.0.105',
        port: 22,
        user: 'root',
        password: '123456'
      },
      loading: false
    }
  },
  props: {
    os: {
      type: String,
      default: null
    }
  },
  mounted() {
  },
  methods: {
    close() {
      this.$emit('close')
    },
    onSubmit() {
      const app = this;
      app.loading = true;
      const url = '/api/session/add';
      const params = {
        ...app.formData_,
        os: app.os
      };
      app.$axios.post(url, params)
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.$emit('success', res.data.data);
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'center',
              multiLine: true,
              closeBtn: true,
              timeout: 30000,
              message: res.data.message
            });
          }
        })
        .catch(e => {
          app.loading = false;
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'could not create session: ' + e
          });
        })
    },
    onReset() {
      this.formData_ = {}
    }
  }
}
</script>

<style scoped>

</style>
