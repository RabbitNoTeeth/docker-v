<template>
  <q-dialog v-model="show" persistent transition-show="scale" transition-hide="scale">
    <q-card style="width: 560px">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">run a new container</div>
        <q-space/>
        <q-btn icon="close" flat round dense v-close-popup @click="close"/>
      </q-card-section>
      <q-form
        @submit="onSubmit"
        @reset="onReset"
        class="q-gutter-md"
        style="width: 500px"
      >
        <q-card-section style="max-height: 50vh;width: 550px" class="scroll">
          <template v-if="!image">
            <div class="text-h6">Select the image:</div>
            <q-select
              filled
              v-model="formData_.imageId"
              :options="images"
              option-value="value"
              option-label="label"
              label="Image *"
              emit-value
              map-options
              lazy-rules
              :rules="[val => val !== null && val !== '' || 'please select the image']"
            />
          </template>
          <template>
            <div class="text-h6">Input the name of the new container:</div>
            <q-input
              filled
              v-model="formData_.name"
              label="name *"
              lazy-rules
              :rules="[ val => val && val.length > 0 || 'please input the name of new container']"
              style="margin-top: 10px"
            />
          </template>
          <div class="text-h6">
            <div>Input options (optional):</div>
          </div>
          <div class="text-h7">
            <div>
              <span>if you think this mode is not easy to use, you can </span>
              <span class="change-mode"
                    @click="optionsFlat = !optionsFlat">change to the {{ optionsFlat ? 'multi' : 'flat' }} mode</span>
            </div>
          </div>
          <template v-if="optionsFlat">
            <q-input
              filled
              v-model="formData_.options"
              label="options"
              style="margin-top: 10px"
            />
          </template>
          <template v-else>
            <div class="row" v-for="(option, index) in options" :key="index" style="margin-top: 10px">
              <q-input
                filled
                v-model="option.name"
                label="option"
              />
              <div style="padding-top: 15px;margin: 0 5px">
                <q-icon name="fas fa-minus"/>
              </div>
              <q-input
                filled
                v-model="option.value"
                label="value"
              />
              <q-btn v-if="options.length !== 1 || index !== 0" icon="fas fa-minus" color="negative" size="sm"
                     @click="onRemoveOptionClick(index)" style="margin-left: 5px"/>
              <q-btn v-if="index === options.length - 1" icon="fas fa-plus" color="secondary" size="sm"
                     @click="onAddOptionClick" style="margin-left: 5px"/>
            </div>
          </template>
        </q-card-section>
        <q-card-section style="width: 550px">
          <div style="text-align: right">
            <q-btn label="submit" type="submit" color="primary"/>
            <q-btn label="reset" type="reset" color="primary" flat class="q-ml-sm"/>
          </div>
        </q-card-section>
      </q-form>
      <q-inner-loading :showing="loading">
        <div class="row">
          <q-spinner-gears size="50px" color="primary"/>
          <span style="margin-left: 15px;font-size: 26px;color: #1976d2">running...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "ContainerRunForm",
  data() {
    return {
      show: true,
      formData_: {},
      loading: false,
      options: [
        {
          name: '',
          value: ''
        }
      ],
      optionsFlat: false,
      images: []
    }
  },
  props: {
    image: {
      type: Object
    }
  },
  inject: ['sessionId'],
  mounted() {
    if (!this.image) {
      this.queryImageList();
    }
  },
  methods: {
    queryImageList() {
      const app = this;
      app.$axios.get('/api/image/list', {
        params: {
          sessionId: app.sessionId
        }
      })
        .then(res => {
          if (res.data.success) {
            app.images = res.data.data.map(image => image.REPOSITORY + ' : ' + image.TAG);
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'center',
              multiLine: true,
              closeBtn: true,
              timeout: 30000,
              message: 'could not load images: ' + res.data.message
            });
          }
        })
        .catch(e => {
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'could not load images. ' + e
          });
        })
    },
    close() {
      this.$emit('close')
    },
    onSubmit() {
      const app = this;
      app.loading = true;
      const url = '/api/container/run';
      const options = app.optionsFlat ? app.formData_.options : app.options.map(o => o.name + ' ' + o.value).join(' ');
      const params = {
        sessionId: app.sessionId,
        imageId: app.image ? app.image.IMAGE_ID : app.formData_.imageId,
        name: app.formData_.name,
        options
      };
      app.$axios.post(url, params)
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.$q.notify({
              type: 'positive',
              position: 'top',
              message: 'run success'
            });
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
            message: 'could not run image: ' + e
          });
        })
    },
    onReset() {
      this.formData_ = {}
      this.options = [{
        name: '',
        value: ''
      }];
    },
    onAddOptionClick() {
      this.options.push({
        name: '',
        value: ''
      });
    },
    onRemoveOptionClick(index) {
      this.options.splice(index, 1);
    }
  }
}
</script>

<style scoped>

.change-mode {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}

</style>
