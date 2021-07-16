<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card>
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">new image</div>
        <q-space/>
        <q-btn icon="close" flat round dense v-close-popup @click="close"/>
      </q-card-section>
      <q-card-section>
        <div>

        </div>
      </q-card-section>
      <q-card-section>
        <q-form
          @submit="onSubmit"
          @reset="onReset"
          class="q-gutter-md"
          style="width: 500px"
        >
          <q-splitter
            v-model="splitterModel"
          >

            <template v-slot:before>
              <q-tabs
                v-model="tab"
                vertical
                class="text-teal"
              >
                <q-tab name="pull" icon="fas fa-cloud-download-alt" label="Pull"/>
                <q-tab name="load" icon="fas fa-file-archive" label="Load"/>
                <q-tab name="build" icon="fas fa-file" label="Build"/>
              </q-tabs>
            </template>

            <template v-slot:after>
              <q-tab-panels
                v-model="tab"
                animated
                swipeable
                vertical
                transition-prev="jump-up"
                transition-next="jump-up"
              >
                <q-tab-panel name="pull">
                  <div class="text-h7">Pull an image or a repository from a registry.</div>
                  <q-input
                    v-if="tab === 'pull'"
                    filled
                    v-model="formData_.image"
                    label="image *"
                    lazy-rules
                    :rules="[ val => val && val.length > 0 || 'please input the image name']"
                    style="margin-top: 20px"
                  />
                </q-tab-panel>
                <q-tab-panel name="load">
                  <div class="text-h7">Load an image from a tar archive or STDIN.</div>
                  <q-input
                    v-if="tab === 'load'"
                    filled
                    v-model="formData_.tarFilePath"
                    label="path of tar file *"
                    lazy-rules
                    :rules="[ val => val && val.length > 0 || 'please input the path of tar file']"
                    style="margin-top: 20px"
                  />
                </q-tab-panel>
                <q-tab-panel name="build">
                  <div class="text-h7">Build an image from a Dockerfile.</div>
                  <q-input
                    v-if="tab === 'build'"
                    filled
                    v-model="formData_.dockerfileDirPath"
                    label="dictionary of Dockerfile *"
                    lazy-rules
                    :rules="[ val => val && val.length > 0 || 'please input the dictionary path of Dockerfile']"
                    style="margin-top: 20px"
                  />
                </q-tab-panel>
              </q-tab-panels>
            </template>
          </q-splitter>
          <div style="text-align: right">
            <q-btn label="submit" type="submit" color="primary"/>
            <q-btn label="reset" type="reset" color="primary" flat class="q-ml-sm"/>
          </div>
        </q-form>
      </q-card-section>
      <q-inner-loading :showing="loading">
        <div class="row">
          <q-spinner-gears size="50px" color="primary" />
          <span v-if="tab === 'pull'" style="margin-left: 15px;font-size: 26px;color: #1976d2">pulling...</span>
          <span v-if="tab === 'load'" style="margin-left: 15px;font-size: 26px;color: #1976d2">loading...</span>
          <span v-if="tab === 'build'" style="margin-left: 15px;font-size: 26px;color: #1976d2">building...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "ImageAddForm",
  data() {
    return {
      show: true,
      formData_: {},
      splitterModel: 20,
      tab: 'pull',
      loading: false
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
      let url = '';
      let params = {
        sessionId: app.sessionId
      };
      const tab = app.tab;
      switch (tab) {
        case 'pull':
          url = '/api/image/pull';
          params.image = app.formData_.image;
          break;
        case 'load':
          url = '/api/image/load';
          params.tarFilePath = app.formData_.tarFilePath;
          break;
        case 'build':
          url = '/api/image/build';
          params.dockerfileDirPath = app.formData_.dockerfileDirPath;
          break;
        default:
          return;
      }

      app.$axios.post(url, params)
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.$emit('success');
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
            message: 'could not add image: ' + e
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
