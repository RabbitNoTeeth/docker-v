<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card>
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">save image</div>
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
            v-model="formData_.savePath"
            label="savePath * (eg: /home/xxx.tar)"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'please input the path of the tar file']"
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
          <span style="margin-left: 15px;font-size: 26px;color: #1976d2">saving...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "ImageSaveForm",
  data() {
    return {
      show: true,
      formData_: {},
      loading: false
    }
  },
  props: {
    image: {
      type: Object
    }
  },
  inject: ['sessionId'],
  mounted() {
  },
  methods: {
    close() {
      this.$emit('close')
    },
    onSubmit() {
      const app = this;
      app.loading = true;
      const url = '/api/image/save';
      const params = {
        sessionId: app.sessionId,
        repository: app.image.REPOSITORY,
        tag: app.image.TAG,
        savePath: app.formData_.savePath
      };
      app.$axios.post(url, params)
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.$emit('success', res.data.data);
            app.$q.notify({
              type: 'positive',
              position: 'top',
              message: 'save success'
            });
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
            message: 'could not save image: ' + e
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
