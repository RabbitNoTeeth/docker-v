<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card>
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">new session</div>
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
            :rules="[ val => val && val.length > 0 || 'please input the ssh port']"
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
            <q-btn label="提交" type="submit" color="primary"/>
            <q-btn label="重置" type="reset" color="primary" flat class="q-ml-sm"/>
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "SessionForm",
  data() {
    return {
      show: true,
      formData_: {}
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
      const url = '/api/session/add';
      const params = {
        ...app.formData_,
        os: app.os
      };
      app.$axios.post(url, params)
        .then(res => {
          if (res.data.success) {
            app.$emit('success', res.data.data);
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'top',
              message: 'could not create session: ' + res.data.message
            });
          }
        })
        .catch(e => {
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
