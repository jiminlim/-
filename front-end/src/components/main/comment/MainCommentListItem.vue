<template>
  <div>
    <div class="row d-flex align-center">
      <div class="col-9">
        <v-avatar @click="goUserDetail(comment.r_email)">
          <img v-if="profileImage" :src="profileImage" alt="User">
          <img v-else src="@/assets/image/user_default.png" alt="User">
        </v-avatar>
        <div 
          class="font-weight-bold ml-4 comment-user my-3"
          @click="goUserDetail(comment.r_email)">{{ comment.r_name }}</div>
        <div class="text-secondary comment-date ml-3">{{ updateTime }}</div>
      </div>
        
      <div class="col-3 pl-2">
        <div class="d-flex flex-wrap justify-content-between">
          <div v-if="currentUser.u_isAdmin === 1" class="ml-auto">
            <v-btn depressed outlined color="error" @click="deleteComment">삭제</v-btn>
          </div>
          <div  v-else-if="comment.r_name === currentUser.u_name" class="ml-auto">
            <v-btn depressed outlined color="error" @click="deleteComment">삭제</v-btn>
          </div>
        </div>
      </div>
    </div>
        <div class="ml-10 pl-10">
          <div class="comment-content">{{ comment.r_content }}</div>
        </div>
    <br>
    <hr>
  </div>
</template>

<script>
import axios from 'axios'
import SERVER from '@/api/drf'

export default {
  name: 'MainCommentListItem',
  props: {
    comment: Object,
    currentUser:Object,
  },
  data(){
    return {
      isAuth: false,
      axiosConfig :{
          headers:{
              token : `${this.$cookies.get('auth-token')}`
          },
      }
    }
  },
  computed: {
    profileImage(){
      return this.comment.r_profileImg
    },
    updateTime(){
      return this.comment.r_date.slice(0,10)
    },
  },

  methods: {
    deleteComment(){
      const idx = this.comment.r_idx
      this.$confirm(
        {
          message: `삭제하시겠습니까?`,
          button: {
            yes: '삭제하기',
            no: '아니요',
          },
          callback: confirm => {
            if (confirm) {
                  this.$emit('delete-comment2',idx)
            }
          }
        }
      )
    },
    checkAuth(){
      if (this.$cookies.get('auth-token')){
        axios.get(SERVER.URL+`/user/getUserInfo`,this.axiosConfig)
        .then((reaponse)=>{
            const currentUser = reaponse.data.u_name
            if(Number(reaponse.data.u_isAdmin)){
                this.isAuth = true
            }else{
                if (this.comment.r_name === currentUser){
                    this.isAuth = true
                }
            }
        })
        .catch((err)=>{
            console.error(err)
        })
      }
    },
    goUserDetail(userId) {
      this.$router.push({ name: 'UserDetailView', params: { userId: userId }})
    },
  },
  mounted(){
    this.checkAuth()
  },
}
</script>

<style>
.comment-user {
  font-size: 16px;
  display: inline-block;
}
.comment-date {
  font-size: 12px;
  display: inline-block;
}
.comment-content {
  font-size: 20px;
}
</style>