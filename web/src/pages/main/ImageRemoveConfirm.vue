<template>
  <q-dialog v-model="show" persistent>
    <q-card style="min-width: 300px">
      <q-card-section class="row items-center">
        <q-icon name="warning" color="negative" style="font-size: 2em;" />
        <span style="font-weight: bold;font-size: 18px;margin-left: 10px;color: red">are you sure to remove this image ?</span>
      </q-card-section>
      <q-card-section class="row items-center">
        <div style="padding-left: 10px">
          <p>
            <span>●&nbsp;</span>
            <span>{{ data.IMAGE_ID }} - {{ data.REPOSITORY }}</span>
          </p>
        </div>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn size="sm" label="yes" color="primary" @click="submit"/>
        <q-btn size="sm" flat label="no" color="primary" @click="close" />
      </q-card-actions>
      <q-inner-loading :showing="loading">
        <div class="row">
          <q-spinner-gears size="50px" color="primary" />
          <span style="margin-left: 15px;font-size: 26px;color: #1976d2">removing...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "ImageRemoveConfirm",
  data() {
    return {
      show: true,
      loading: false
    }
  },
  props: {
    data: {
      type: Object
    }
  },
  inject: ['sessionId'],
  methods: {
    close() {
      this.$emit('close')
    },
    submit() {
      const app = this;
      app.loading = true;
      const url = '/api/image/remove';
      app.$axios.post(url, {
        sessionId: app.sessionId,
        imageId: app.data.IMAGE_ID
      })
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.$emit('success');
            app.$q.notify({
              type: 'positive',
              position: 'top',
              message: 'remove success'
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
            message: 'could not remove the image. ' + e
          });
        })
    }
  }
}
</script>

<style scoped>

</style>
