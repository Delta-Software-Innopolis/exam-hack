<script setup lang="ts">
import { ref, nextTick, watch} from "vue";
import BasicButton from "@/components/basic/BasicButton.vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
const router = useRouter();

const userStore = useUserStore()
const showGlitter = ref(false);
const glitterVideo = ref<HTMLVideoElement | null>(null);
watch(showGlitter, (new_value, old_value) => {
  if (!new_value && old_value === true) {
    router.push("/")
  }
})
async function glitter() {
  showGlitter.value = true;

  await nextTick();

  if (glitterVideo.value) {
    glitterVideo.value.currentTime = 0;
    glitterVideo.value.style.opacity = '1';
    await glitterVideo.value.play();

    glitterVideo.value.addEventListener('timeupdate', () => {
      if (glitterVideo.value!.currentTime >= 0.75) {
        glitterVideo.value!.style.opacity = '0';
      }
      if (glitterVideo.value!.currentTime >= 1) {
        glitterVideo.value!.pause();
        showGlitter.value = false;
      }
    });
  }
}

function onGlitterEnded() {
  showGlitter.value = false;
}
</script>

<template>
  <div v-if="showGlitter" class="glitter-overlay">
    <video
      ref="glitterVideo"
      class="glitter-video"
      src="/fire.webm"
      autoplay
      muted
      playsinline
      preload="auto"
      disablePictureInPicture
      controlsList="nodownload nofullscreen noplaybackrate"
      @ended="onGlitterEnded"
    />
  </div>

  <div class="window-wrapper">
    <div class="sidebar">
      <div class="text-wrapper">
        <h1 v-if="userStore.isNew">Thank you for joining, {{userStore.username}}!</h1>
        <h1 v-else>Welcome back, {{ userStore.username }}!</h1>


        <p>end of demo 0</p>
      </div>

      <div class="buttons-wrapper">
        <BasicButton variant="yellow" @click="glitter">
          Sit back and relax
        </BasicButton>
      </div>
    </div>

    <div class="backbone">
      <img src="/amazing_things_soon.png" alt="amazing_things_soon" />
    </div>
  </div>
</template>

<style>
.glitter-overlay {
  position: fixed;
  inset: 0;
  z-index: 999999;
  pointer-events: none;
}

.glitter-video {
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  transition: opacity 0.5s;
  opacity: 1;
}

/* existing styles below */
.window-wrapper {
  display: flex;
  flex-direction: row;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  justify-content: space-evenly;
}

.window-wrapper {
  display: flex;
  flex-direction: row;
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  justify-content: space-evenly;
}
.text-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
  align-items: end;
}
.sidebar p {
  color: #b3b3b3;
  padding: 0;
  margin: 0;
  margin-top: -0.5em;
}
.sidebar {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 32px;
  justify-content: center;
  align-items: center;
  background-color: white;
  box-shadow: 0px 0px 16px 4px rgba(0, 0, 0, 0.25);
  animation: bounce 1s;
}
.sidebar * {
  animation: appear 0.5s;
}
.buttons-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: fit-content;
  width: fit-content;
}
.inputs-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: fit-content;
  width: fit-content;
}
.inputs-wrapper * {
  height: 48px;
  width: 387px;
  font-size: 18px;
}
.buttons-wrapper button {
  height: 48px;
  width: 387px;
  font-size: 18px;
}
.backbone {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}
.backbone img {
  scale: 0.75;
  animation: appear 1s;
}
@keyframes bounce {
  0% {
    padding-left: 0;
  }
  25% {
    padding-left: 5%;
  }
  100% {
    padding-left: 0;
  }
}
@keyframes appear {
  0% {
    opacity: 0%;
  }
  100% {
    opacity: 100%;
  }
}
</style>
