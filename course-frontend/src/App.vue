<template>
  <div class="main-app">
    <div v-if="!isLoggedIn" class="login-wrapper">
      <div class="login-overlay"></div>
      <el-card class="modern-auth-card" v-if="!isRegisterMode && !isResetMode">
        <div class="auth-header">
          <span class="auth-logo">🎓</span>
          <h2 class="text-h2">智能教务系统</h2>
          <p class="text-sm" style="margin-top: var(--space-2);">开启现代校园管理新体验</p>
        </div>
        <el-form :model="loginForm" @keyup.enter="handleLogin">
          <el-form-item><el-input v-model="loginForm.username" placeholder="账号" size="large" /></el-form-item>
          <el-form-item><el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password /></el-form-item>
          <div style="text-align:right; margin-bottom: var(--space-3);"><el-link type="info" @click="isResetMode=true" class="text-xs">忘记密码？</el-link></div>
          <el-button type="primary" size="large" style="width:100%;" :loading="isLoggingIn" @click="handleLogin">立即登录</el-button>
          <div style="text-align:center; margin-top: var(--space-4);"><el-link type="default" @click="isRegisterMode=true" class="text-sm font-medium">注册新用户 &rarr;</el-link></div>
        </el-form>
      </el-card>

      <el-card class="modern-auth-card register-card" v-else-if="isRegisterMode">
        <div class="auth-header"><h2 class="text-h2">✨ 注册新用户</h2></div>
        <el-form :model="registerForm" label-position="top">
          <el-row :gutter="24"><el-col :span="12"><el-form-item label="身份"><el-radio-group v-model="registerForm.role"><el-radio label="STUDENT">学生</el-radio><el-radio label="TEACHER">教师</el-radio></el-radio-group></el-form-item></el-col><el-col :span="12"><el-form-item label="姓名"><el-input v-model="registerForm.name" /></el-form-item></el-col></el-row>
          <el-row :gutter="24"><el-col :span="12"><el-form-item label="账号"><el-input v-model="registerForm.username" /></el-form-item></el-col><el-col :span="12"><el-form-item label="密码"><el-input v-model="registerForm.password" type="password" show-password /></el-form-item></el-col></el-row>
          <el-row :gutter="24"><el-col :span="12"><el-form-item :label="registerForm.role==='STUDENT'?'学号':'工号'"><el-input v-model="registerForm.userNo" /></el-form-item></el-col><el-col :span="12"><el-form-item label="联系方式"><el-input v-model="registerForm.phone" placeholder="用于找回密码" /></el-form-item></el-col></el-row>
          <el-row :gutter="24" v-if="registerForm.role==='STUDENT'"><el-col :span="12"><el-form-item label="所属专业"><el-input v-model="registerForm.major" placeholder="如：软件工程" /></el-form-item></el-col><el-col :span="12"><el-form-item label="所在班级"><el-input v-model="registerForm.className" placeholder="如：软工2401班" /></el-form-item></el-col></el-row>
          <el-form-item label="教师介绍" v-if="registerForm.role==='TEACHER'"><el-input type="textarea" v-model="registerForm.intro" /></el-form-item>
          <el-button type="primary" size="large" style="width:100%;" :loading="isRegistering" @click="handleRegister">提交注册</el-button>
          <div style="text-align:center; margin-top: var(--space-4);"><el-link type="info" @click="isRegisterMode=false" class="text-sm">返回登录</el-link></div>
        </el-form>
      </el-card>
      
      <el-card class="modern-auth-card" v-else-if="isResetMode">
        <div class="auth-header"><h2 class="text-h2">🔑 找回密码</h2></div>
        <el-form :model="resetForm"><el-form-item><el-input v-model="resetForm.username" placeholder="账号" size="large" /></el-form-item><el-form-item><el-input v-model="resetForm.phone" placeholder="预留联系方式" size="large" /></el-form-item><el-form-item><el-input v-model="resetForm.newPassword" type="password" placeholder="新密码" size="large" show-password /></el-form-item><el-button type="primary" size="large" style="width:100%;" :loading="isResetting" @click="handleResetPassword">确认重置</el-button><div style="text-align:center; margin-top: var(--space-4);"><el-link type="info" @click="isResetMode=false" class="text-sm">返回登录</el-link></div></el-form>
      </el-card>
    </div>

    <div v-else class="app-layout">
      <header class="main-header">
        <div class="header-left"><span class="app-icon">🎓</span><h1 class="text-h3" style="margin:0;">教务管理系统</h1></div>
        <div class="header-menu">
          <el-menu :default-active="activeMenu" mode="horizontal" @select="handleMenuSelect" :ellipsis="false">
            <el-menu-item index="home" class="text-sm">首页</el-menu-item>
            
            <el-menu-item index="hall" v-if="userRole === 'STUDENT'" class="text-sm">选课大厅</el-menu-item>
            <el-menu-item index="my" v-if="userRole === 'STUDENT'" class="text-sm">课表管理</el-menu-item>
            <el-menu-item index="studentGrades" v-if="userRole === 'STUDENT'" class="text-sm">成绩查询</el-menu-item>
            <el-menu-item index="studentAttendance" v-if="userRole === 'STUDENT'" class="text-sm">考勤记录</el-menu-item>
            <el-menu-item index="studentEval" v-if="userRole === 'STUDENT'" class="text-sm">教学评估</el-menu-item>
            <el-menu-item index="ai" v-if="userRole === 'STUDENT'" class="text-sm">AI 助手</el-menu-item>
            
            <el-menu-item index="teacherCourses" v-if="userRole === 'TEACHER'" class="text-sm">授课管理</el-menu-item>
            <el-menu-item index="teacherGrades" v-if="userRole === 'TEACHER'" class="text-sm">成绩录入</el-menu-item>
            <el-menu-item index="teacherQuality" v-if="userRole === 'TEACHER'" class="text-sm">教学质量</el-menu-item>
            
            <el-menu-item index="adminUsers" v-if="userRole === 'ADMIN'" class="text-sm">账号管理</el-menu-item>
            <el-menu-item index="adminCourses" v-if="userRole === 'ADMIN'" class="text-sm">全局课程</el-menu-item>
            <el-menu-item index="adminClassrooms" v-if="userRole === 'ADMIN'" class="text-sm">教室管理</el-menu-item>
            <el-menu-item index="adminBanners" v-if="userRole === 'ADMIN'" class="text-sm">轮播图配置</el-menu-item>
            
            <el-menu-item index="reserveClassroom" v-if="userRole === 'STUDENT' || userRole === 'TEACHER'" class="text-sm">查空教室与预约</el-menu-item>
            <el-menu-item index="forum" v-if="userRole === 'STUDENT' || userRole === 'TEACHER' || userRole === 'ADMIN'" class="text-sm">讨论论坛</el-menu-item>
            <el-menu-item index="chat" v-if="userRole === 'STUDENT' || userRole === 'TEACHER'" class="text-sm"><el-badge :value="globalUnreadCount" :max="99" :hidden="globalUnreadCount === 0" class="nav-badge">师生交流</el-badge></el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info" style="cursor: pointer;"><span class="user-name text-sm font-medium">{{ userName }}</span><el-tag size="small" type="info" effect="plain">{{ userRole }}</el-tag></div>
            <template #dropdown><el-dropdown-menu><el-dropdown-item command="profile" class="text-sm">个人中心</el-dropdown-item><el-dropdown-item command="logout" divided style="color: var(--danger);" class="text-sm">退出登录</el-dropdown-item></el-dropdown-menu></template>
          </el-dropdown>
        </div>
      </header>

      <main class="main-content">
        <div v-if="activeMenu === 'home'">
          <el-row :gutter="24">
            <el-col :span="userRole === 'STUDENT' ? 16 : 24">
              <el-carousel height="260px" class="dashboard-carousel">
                <el-empty v-if="dynamicBanners.length === 0" description="暂未配置轮播图" style="background: rgba(255,255,255,0.8);" />
                <el-carousel-item v-for="banner in dynamicBanners" :key="banner.id">
                  <div class="banner-content" :style="{ backgroundImage: banner.imageBase64 ? `url(${banner.imageBase64})` : 'linear-gradient(135deg, #18181b 0%, #27272a 100%)', backgroundSize: 'cover', backgroundPosition: 'center' }">
                    <div class="banner-mask">
                      <h2 class="text-h2 text-white">{{ banner.title }}</h2>
                      <p class="text-body text-white" style="margin-top: var(--space-2); opacity: 0.9;">{{ banner.sub }}</p>
                    </div>
                  </div>
                </el-carousel-item>
              </el-carousel>
            </el-col>
            <el-col :span="8" v-if="userRole === 'STUDENT'">
              <el-card class="recommend-panel" style="height: 260px;">
                <div class="card-header" style="margin-bottom: var(--space-3);"><h3 class="text-h3">AI 猜你喜欢</h3></div>
                <div class="recommend-scroll">
                  <div v-for="item in recommendList" :key="item.id" class="recommend-mini-card" @click="showDetail(item.id)">
                    <div class="text-body font-medium" style="margin-bottom: var(--space-1);">{{ item.title }}</div>
                    <div class="text-xs" style="margin-bottom: var(--space-2);">{{ item.courseTime }} &middot; {{ item.teacher }}</div>
                    <el-button v-if="(item.courseType || item.course_type) !== 'REQUIRED'" size="small" style="width: 100%;" @click.stop="handleEnroll(item.id)">选修此课</el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row style="margin-top: var(--space-6);">
            <el-col :span="24">
              <el-card class="matrix-card" v-if="userRole === 'STUDENT'">
                <div class="card-header">
                  <div style="display: flex; align-items: center; gap: var(--space-4);">
                    <h3 class="text-h3">我的课表</h3>
                    <el-select v-model="selectedWeek" size="small" style="width: 120px;">
                      <el-option v-for="w in 20" :key="w" :label="`第 ${w} 周`" :value="w" />
                    </el-select>
                  </div>
                </div>
                <table class="matrix-table compact-table">
                  <thead><tr><th class="time-th text-xs">节次 \ 星期</th><th v-for="day in weekDays" :key="day" class="text-sm font-medium">{{ day }}</th></tr></thead>
                  <tbody>
                    <tr v-for="slot in timeSlots" :key="slot.id">
                      <td class="time-td"><div class="text-xs font-medium">{{ slot.name }}</div><div class="text-xs" style="color: var(--muted-foreground);">{{ slot.time }}</div></td>
                      <td v-for="day in weekDays" :key="day" class="course-td">
                        <template v-if="getCourseInSlot(day, slot, activeMySchedule)">
                          <div class="course-block student-block" @click="showDetail(getCourseInSlot(day, slot, activeMySchedule).id)">
                            <div class="text-xs font-medium line-clamp">{{ getCourseInSlot(day, slot, activeMySchedule).title }}</div>
                            <div class="text-xs mt-1">{{ getCourseInSlot(day, slot, activeMySchedule).location }}</div>
                          </div>
                        </template>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </el-card>

              <el-card class="matrix-card" v-if="userRole === 'TEACHER'">
                <div class="card-header">
                  <div style="display: flex; align-items: center; gap: var(--space-4);">
                    <h3 class="text-h3">我的授课安排</h3>
                    <el-select v-model="selectedWeek" size="small" style="width: 120px;">
                      <el-option v-for="w in 20" :key="w" :label="`第 ${w} 周`" :value="w" />
                    </el-select>
                  </div>
                </div>
                <table class="matrix-table compact-table">
                  <thead><tr><th class="time-th text-xs">节次 \ 星期</th><th v-for="day in weekDays" :key="day" class="text-sm font-medium">{{ day }}</th></tr></thead>
                  <tbody>
                    <tr v-for="slot in timeSlots" :key="slot.id">
                      <td class="time-td"><div class="text-xs font-medium">{{ slot.name }}</div><div class="text-xs" style="color: var(--muted-foreground);">{{ slot.time }}</div></td>
                      <td v-for="day in weekDays" :key="day" class="course-td">
                        <template v-if="getCourseInSlot(day, slot, activeMyCourses)">
                          <div class="course-block teacher-block" @click="viewStudents(getCourseInSlot(day, slot, activeMyCourses).id, false)">
                            <div class="text-xs font-medium line-clamp">{{ getCourseInSlot(day, slot, activeMyCourses).title }}</div>
                            <div class="text-xs mt-1">{{ getCourseInSlot(day, slot, activeMyCourses).location }}</div>
                          </div>
                        </template>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </el-card>
              
              <div v-if="userRole === 'ADMIN'" style="text-align: center; padding: var(--space-12) 0;">
                <h2 class="text-h2">欢迎进入教务中心控制台</h2><p class="text-body" style="color: var(--muted-foreground); margin-top: var(--space-3);">您拥有最高权限，请通过上方导航栏进行管理操作。</p>
              </div>
            </el-col>
          </el-row>
        </div>

        <div v-if="activeMenu === 'hall' && userRole === 'STUDENT'">
          <el-card>
            <div class="card-header"><div><h3 class="text-h3">选课大厅</h3><p class="text-sm">点击课程名称可查看详细介绍与教材</p></div></div>
            <el-table :data="tableData">
              <el-table-column label="课程名称" min-width="180">
                <template #default="scope">
                  <el-tag v-if="scope.row.status == 2" type="info" size="small" effect="plain" style="margin-right: var(--space-2);">已完结</el-tag>
                  <el-tag :type="(scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? 'danger' : 'success'" size="small" effect="plain" style="margin-right: var(--space-2);">{{ (scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? '必修' : '选修' }}</el-tag>
                  <el-link @click="showDetail(scope.row.id)" class="text-body font-medium" type="primary">{{ scope.row.title }}</el-link>
                </template>
              </el-table-column>
              <el-table-column prop="semester" label="学期" width="140" align="center" />
              <el-table-column prop="teacher" label="授课老师" width="120" align="center" />
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <span v-if="scope.row.status == 2" class="text-xs">已结课</span>
                  <el-button v-else-if="(scope.row.courseType || scope.row.course_type) !== 'REQUIRED'" size="small" @click="handleEnroll(scope.row.id)">选修此课</el-button>
                  <span v-else class="text-xs">系统已排课</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <div v-if="activeMenu === 'my' && userRole === 'STUDENT'">
          <el-card>
            <div class="card-header"><div><h3 class="text-h3">课表与退课管理</h3><p class="text-sm">点击课程名称可查看授课时间地点</p></div></div>
            <el-table :data="activeMySchedule" v-loading="loadingSchedule">
              <el-table-column label="课程名称">
                <template #default="scope">
                  <el-tag :type="(scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? 'danger' : 'success'" size="small" effect="plain" style="margin-right: var(--space-2);">{{ (scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? '必修' : '选修' }}</el-tag>
                  <el-link @click="showDetail(scope.row.id)" class="text-body font-medium" type="primary">{{ scope.row.title }}</el-link>
                </template>
              </el-table-column>
              <el-table-column prop="courseTime" label="排课时间" width="200" />
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-button v-if="(scope.row.courseType || scope.row.course_type) !== 'REQUIRED'" type="danger" size="small" plain @click="handleDrop(scope.row.id)">退课</el-button>
                  <span v-else class="text-xs" style="color: var(--danger);">必修不可退</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <div v-if="activeMenu === 'adminClassrooms' && userRole === 'ADMIN'"><el-card><div class="card-header"><div><h3 class="text-h3">物理教室资源管理</h3><p class="text-sm">添加或移除全校可调度的教室</p></div><el-button type="primary" @click="openAddRoomDialog">新增教室</el-button></div><el-table :data="adminRoomList" v-loading="loadingRooms"><el-table-column prop="room_name" label="教室名称" /><el-table-column prop="capacity" label="容纳人数" width="150" align="center" /><el-table-column label="操作" width="150" align="center"><template #default="scope"><el-button type="danger" size="small" plain @click="handleDeleteRoom(scope.row.id)">删除</el-button></template></el-table-column></el-table></el-card></div>
        
        <div v-if="activeMenu === 'reserveClassroom' && (userRole === 'STUDENT' || userRole === 'TEACHER')">
          <el-card style="margin-bottom: var(--space-6);">
            <div class="card-header"><div><h3 class="text-h3">查空教室与预约</h3><p class="text-sm">选择日期与节次，寻找可用的空闲教室</p></div></div>
            <div style="display: flex; gap: var(--space-4); margin-bottom: var(--space-6);">
              <el-date-picker v-model="reserveDate" type="date" value-format="YYYY-MM-DD" placeholder="选择使用日期" />
              <el-select v-model="reserveSlot" placeholder="选择节次" style="width: 200px;">
                <el-option v-for="s in timeSlots" :key="s.id" :label="s.name + ' (' + s.time + ')'" :value="s.id"></el-option>
              </el-select>
              <el-button type="primary" @click="searchEmptyRooms">查询空教室</el-button>
            </div>
            <el-table :data="emptyRoomList" v-loading="loadingRooms">
              <template #empty><el-empty description="请先选择时间和节次进行查询" /></template>
              <el-table-column prop="room_name" label="空闲教室名称" /><el-table-column prop="capacity" label="容纳人数" width="150" align="center" />
              <el-table-column label="操作" width="150" align="center"><template #default="scope"><el-button type="success" size="small" plain @click="openReserveDialog(scope.row.room_name)">立即预约</el-button></template></el-table-column>
            </el-table>
          </el-card>
          <el-card><div class="card-header"><div><h3 class="text-h3">我的预约记录</h3></div></div><el-table :data="myReservationList"><template #empty><el-empty description="暂无预约记录" /></template><el-table-column prop="reserve_date" label="预约日期" width="150" /><el-table-column prop="time_slot" label="时间段" width="150"><template #default="scope">{{ timeSlots.find(s => s.id === scope.row.time_slot)?.name || scope.row.time_slot }}</template></el-table-column><el-table-column prop="room_name" label="教室名称" width="150" /><el-table-column prop="purpose" label="使用用途" /><el-table-column label="状态" width="100" align="center"><template #default><el-tag type="success" effect="plain">预约成功</el-tag></template></el-table-column></el-table></el-card>
        </div>

        <div v-if="activeMenu === 'adminUsers' && userRole === 'ADMIN'"><el-card><div class="card-header"><div><h3 class="text-h3">师生账号系统管理</h3></div><el-button type="primary" @click="openAddUserDialog">新增账号</el-button></div><el-table :data="adminUserList" v-loading="loadingUsers"><el-table-column prop="role" label="身份" width="100" align="center"><template #default="scope"><el-tag :type="scope.row.role === 'TEACHER' ? 'warning' : 'primary'" effect="plain">{{ scope.row.role === 'TEACHER' ? '教师' : '学生' }}</el-tag></template></el-table-column><el-table-column prop="username" label="登录账号" width="120" /><el-table-column prop="name" label="真实姓名" width="120" /><el-table-column prop="userNo" label="学工号" width="120" /><el-table-column prop="major" label="专业" /><el-table-column prop="className" label="班级" width="120" /><el-table-column label="操作" width="120" align="center"><template #default="scope"><el-button type="danger" size="small" plain @click="handleDeleteUser(scope.row.id)">注销</el-button></template></el-table-column></el-table></el-card></div>
        <div v-if="activeMenu === 'adminBanners' && userRole === 'ADMIN'"><el-card><div class="card-header"><div><h3 class="text-h3">首页图片轮播配置</h3></div><el-button type="primary" @click="openBannerDialog()">添加轮播图片</el-button></div><el-table :data="adminBannerList"><el-table-column prop="title" label="主标题" width="200" /><el-table-column prop="sub" label="副标题" /><el-table-column label="图片预览" width="180" align="center"><template #default="scope"><el-image v-if="scope.row.imageBase64" style="width: 120px; height: 60px; border-radius: var(--radius-sm);" :src="scope.row.imageBase64" fit="cover" /><span v-else class="text-xs">无图片</span></template></el-table-column><el-table-column label="操作" width="180" align="center"><template #default="scope"><el-button type="danger" size="small" plain @click="handleDeleteBanner(scope.row.id)">删除</el-button></template></el-table-column></el-table></el-card></div>

        <div v-if="activeMenu === 'studentAttendance' && userRole === 'STUDENT'"><el-card><div class="card-header"><div><h3 class="text-h3">我的考勤记录</h3></div></div><el-table :data="myAttendanceList" v-loading="loadingAttendance"><template #empty><el-empty description="暂无考勤记录" /></template><el-table-column prop="recordDate" label="考勤日期" width="160" align="center" /><el-table-column prop="courseName" label="课程名称" /><el-table-column label="考勤状态" width="150" align="center"><template #default="scope"><el-tag v-if="scope.row.status === 'PRESENT'" type="success" effect="plain">正常</el-tag><el-tag v-else-if="scope.row.status === 'LATE'" type="warning" effect="plain">迟到</el-tag><el-tag v-else-if="scope.row.status === 'LEAVE'" type="info" effect="plain">请假</el-tag><el-tag v-else-if="scope.row.status === 'ABSENT'" type="danger" effect="plain">缺勤</el-tag></template></el-table-column></el-table></el-card></div>
        <div v-if="activeMenu === 'studentGrades' && userRole === 'STUDENT'"><el-card><div class="card-header"><div><h3 class="text-h3">我的成绩单</h3></div><el-select v-model="selectedSemester" placeholder="全部学期" @change="loadMyGrades" style="width: 180px;"><el-option label="全部学期" value="ALL"></el-option><el-option label="2025-2026春季" value="2025-2026春季"></el-option><el-option label="2025-2026秋季" value="2025-2026秋季"></el-option></el-select></div><el-table :data="myGradeList" v-loading="loadingGrades"><el-table-column prop="semester" label="开课学期" width="160" align="center" /><el-table-column label="课程名称"><template #default="scope"><div class="text-body font-medium">{{ scope.row.courseName }}</div></template></el-table-column><el-table-column prop="credits" label="学分" width="100" align="center" /><el-table-column label="期末成绩" width="150" align="center"><template #default="scope"><span v-if="scope.row.grade !== null" class="text-h3">{{ scope.row.grade }}</span><span v-else class="text-xs">暂未出分</span></template></el-table-column><el-table-column label="状态" width="100" align="center"><template #default="scope"><el-tag v-if="scope.row.grade !== null && scope.row.grade >= 60" type="success" effect="plain">及格</el-tag><el-tag v-else-if="scope.row.grade !== null && scope.row.grade < 60" type="danger" effect="plain">挂科</el-tag><span v-else class="text-xs">-</span></template></el-table-column></el-table></el-card></div>
        <div v-if="activeMenu === 'studentEval' && userRole === 'STUDENT'"><el-card><div class="card-header"><div><h3 class="text-h3">课程质量评估</h3></div></div><el-table :data="myGradeList.filter(c => c.status == 2)" v-loading="loadingGrades"><el-table-column prop="semester" label="学期" width="150" align="center" /><el-table-column prop="courseName" label="课程名称" /><el-table-column prop="teacher" label="授课教师" width="120" align="center" /><el-table-column label="操作状态" width="180" align="center"><template #default="scope"><el-button v-if="scope.row.evalScore == null" type="primary" size="small" @click="openEvalDialog(scope.row)">去评价</el-button><div v-else class="text-sm font-medium" style="color: var(--success);">已评教 ({{ scope.row.evalScore }}分)</div></template></el-table-column></el-table></el-card></div>

        <div v-if="(activeMenu === 'teacherCourses' && userRole === 'TEACHER') || (activeMenu === 'adminCourses' && userRole === 'ADMIN')">
          <el-card>
            <div class="card-header"><div><h3 class="text-h3">授课与全局课程管理</h3></div><el-button v-if="userRole === 'TEACHER'" type="primary" @click="openAddCourseDialog">发布新课程</el-button></div>
            <el-table :data="userRole === 'ADMIN' ? adminCourseList : myCourseList" v-loading="loadingCourses">
              <el-table-column prop="title" label="课程名称"><template #default="scope"><el-tag :type="(scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? 'danger' : 'success'" size="small" effect="plain" style="margin-right: var(--space-2);">{{ (scope.row.courseType || scope.row.course_type) === 'REQUIRED' ? '必修' : '选修' }}</el-tag><span class="text-body font-medium">{{ scope.row.title }}</span></template></el-table-column>
              <el-table-column label="状态" width="100" align="center"><template #default="scope"><el-tag :type="scope.row.status == 2 ? 'info' : 'success'" effect="plain">{{ scope.row.status == 2 ? '已完结' : '进行中' }}</el-tag></template></el-table-column>
              <el-table-column label="操作" width="300" align="center"><template #default="scope"><el-button v-if="scope.row.status != 2 && userRole === 'TEACHER'" type="success" size="small" plain @click="openAttendanceDialog(scope.row.id)">考勤</el-button><el-button v-if="scope.row.status != 2 && userRole === 'TEACHER'" size="small" @click="handleComplete(scope.row.id)">结课</el-button><el-button type="default" size="small" plain @click="openEditCourse(scope.row)">编辑</el-button><el-button type="default" size="small" @click="viewStudents(scope.row.id, false)" v-if="userRole === 'TEACHER'">名单</el-button><el-button type="danger" size="small" plain @click="handleDeleteCourse(scope.row.id)" v-if="userRole === 'ADMIN'">删除</el-button></template></el-table-column>
            </el-table>
          </el-card>
        </div>

        <div v-if="activeMenu === 'teacherGrades' && userRole === 'TEACHER'"><el-card><div class="card-header"><div><h3 class="text-h3">成绩录入与归档</h3></div></div><el-table :data="myCourseList.filter(c => c.status == 2)" v-loading="loadingCourses"><template #empty><el-empty description="暂无已完结的课程" /></template><el-table-column prop="semester" label="开课学期" width="180" align="center" /><el-table-column prop="title" label="已完结课程名称" /><el-table-column label="操作" width="180" align="center"><template #default="scope"><el-button type="primary" size="small" @click="viewStudents(scope.row.id, true)">进入录分通道</el-button></template></el-table-column></el-table></el-card></div>
        <div v-if="activeMenu === 'teacherQuality' && userRole === 'TEACHER'"><el-card><div class="card-header"><div><h3 class="text-h3">教学质量评估报告</h3></div></div><el-table :data="myCourseList.filter(c => c.status == 2)" v-loading="loadingCourses"><template #empty><el-empty description="需等待课程完结并有学生参与评价" /></template><el-table-column prop="semester" label="学期" width="150" align="center" /><el-table-column prop="title" label="课程名称" /><el-table-column label="操作" width="200" align="center"><template #default="scope"><el-button type="default" size="small" @click="viewEvalStats(scope.row)">查看报告</el-button></template></el-table-column></el-table></el-card></div>

        <div v-if="activeMenu === 'forum'"><div class="forum-header-bar"><el-input v-model="forumSearchKeyword" placeholder="搜索帖子..." size="large" style="width: 400px; border-radius: var(--radius-md);" @keyup.enter="loadForumData"><template #append><el-button @click="loadForumData">搜索</el-button></template></el-input><el-button type="primary" size="large" @click="openPostDialog">{{ userRole === 'ADMIN' ? '发布公告' : '发布动态' }}</el-button></div><div class="forum-container" v-loading="loadingForum"><el-empty v-if="forumList.length === 0" description="暂无帖子" /><el-card v-for="post in forumList" :key="post.id" class="forum-post-card"><div class="post-header"><el-avatar :size="40" :style="{ background: post.authorUsername === 'admin' ? 'var(--primary)' : 'var(--border)', color: post.authorUsername === 'admin' ? 'white' : 'var(--foreground)' }">{{ post.authorName ? post.authorName.substring(0,1) : 'U' }}</el-avatar><div class="post-meta"><div class="post-author"><el-tag v-if="post.authorUsername === 'admin' || post.authorName.includes('管理员')" type="info" size="small" effect="plain" style="margin-right: var(--space-2);">官方</el-tag><span class="text-body font-medium">{{ post.authorName }}</span></div><div class="text-xs" style="margin-top: var(--space-1);">{{ post.createTime ? post.createTime.replace('T', ' ') : '刚刚' }}</div></div></div><div class="post-body"><div class="text-body" style="white-space: pre-wrap; margin-bottom: var(--space-3);">{{ post.content }}</div><img v-if="post.imageBase64" :src="post.imageBase64" class="post-image" alt="帖子图片" /></div><div class="post-footer"><div class="post-actions"><el-button type="default" size="small" @click="handleLikePost(post.id)">👍 <span v-if="post.likes > 0">{{ post.likes }}</span><span v-else>点赞</span></el-button><el-button type="danger" size="small" plain style="margin-left: var(--space-3);" v-if="userRole === 'ADMIN'" @click="handleDeletePost(post.id)">强制下架</el-button></div><div class="comments-section" v-if="post.comments && post.comments.length > 0"><div v-for="c in post.comments" :key="c.id" class="comment-item"><span class="text-sm font-medium">{{ c.authorName }}: </span><span class="text-sm">{{ c.content }}</span></div></div><div class="comment-input-box" v-if="userRole !== 'ADMIN'"><el-input v-model="post.newCommentInput" size="small" placeholder="写下你的评论..." @keyup.enter="submitComment(post)" /><el-button size="small" plain @click="submitComment(post)" style="margin-left: var(--space-2);">发送</el-button></div></div></el-card></div></div>
        
        <div v-if="activeMenu === 'ai' && userRole === 'STUDENT'"><el-card style="height: calc(100vh - 160px); display: flex; flex-direction: column;"><div class="card-header"><h3 class="text-h3">专属 AI 助手</h3></div><div class="ai-chat-container"><div class="chat-history" ref="aiChatBox"><div v-for="(msg, index) in aiChatHistory" :key="index" class="p2p-message-row" :class="msg.role === 'user' ? 'p2p-msg-right' : 'p2p-msg-left'"><el-avatar v-if="msg.role === 'ai'" :size="40" style="background:var(--primary); color:white; margin-right:var(--space-3); flex-shrink: 0;">AI</el-avatar><div class="p2p-bubble text-body" :class="msg.role === 'user' ? 'my-bubble' : 'their-bubble'" style="white-space: pre-wrap;">{{ msg.content }}</div><el-avatar v-if="msg.role === 'user'" :size="40" style="background:var(--success); color:white; margin-left:var(--space-3); flex-shrink: 0;">{{ userName ? userName.substring(0,1) : '我' }}</el-avatar></div><div v-if="aiLoading" class="p2p-message-row p2p-msg-left"><el-avatar :size="40" style="background:var(--primary); color:white; margin-right:var(--space-3);">AI</el-avatar><div class="p2p-bubble their-bubble text-sm">思考中... ⏳</div></div></div><div class="chat-input-area"><el-input v-model="userInput" size="large" @keyup.enter="askAI" placeholder="试试向我提问..."><template #append><el-button @click="askAI" :loading="aiLoading">发送</el-button></template></el-input></div></div></el-card></div>

        <div v-if="activeMenu === 'chat' && (userRole === 'STUDENT' || userRole === 'TEACHER')"><el-card body-style="padding: 0; height: calc(100vh - 160px);"><div class="p2p-chat-layout"><div class="p2p-sidebar"><div class="p2p-sidebar-header text-h3">通讯录</div><div class="p2p-contact-list"><div v-for="contact in contactList" :key="contact.username" class="p2p-contact-item" :class="{ 'active-contact': currentContact?.username === contact.username }" @click="selectContact(contact)"><el-avatar :size="40" class="contact-avatar" :style="{ background: contact.role === 'TEACHER' ? 'var(--primary)' : 'var(--border)', color: contact.role === 'TEACHER' ? 'white' : 'var(--foreground)' }">{{ contact.name ? contact.name.substring(0,1) : 'U' }}</el-avatar><div class="contact-info"><div class="text-body font-medium">{{ contact.name || contact.username }}</div><div class="text-xs">{{ contact.role === 'TEACHER' ? '授课教师' : (contact.major || '学生') }}</div></div></div></div></div><div class="p2p-main"><template v-if="currentContact"><div class="p2p-main-header text-h3" style="display:flex; justify-content:space-between; align-items: center;"><span>{{ currentContact.name }}</span><span class="text-xs" style="color:var(--success);">● 在线</span></div><div class="p2p-chat-window" ref="chatBox"><div v-for="(msg, index) in currentChatHistory" :key="msg.id || index" class="p2p-message-row" :class="msg.sender === loginUsername ? 'p2p-msg-right' : 'p2p-msg-left'"><el-avatar v-if="msg.sender !== loginUsername" :size="36" class="p2p-avatar" style="background: var(--border); color: var(--foreground);">{{ currentContact.name ? currentContact.name.substring(0,1) : 'T' }}</el-avatar><div class="p2p-bubble text-sm" :class="msg.sender === loginUsername ? 'my-bubble' : 'their-bubble'">{{ msg.content }}</div><el-avatar v-if="msg.sender === loginUsername" :size="36" class="p2p-avatar" style="background: var(--primary);">{{ userName ? userName.substring(0,1) : '我' }}</el-avatar></div></div><div class="p2p-input-area"><el-input v-model="chatMessageInput" type="textarea" :rows="3" placeholder="输入消息..." @keyup.enter.exact="sendP2PMessage" style="margin-bottom: var(--space-2);" /><div style="text-align: right;"><el-button type="primary" @click="sendP2PMessage">发送</el-button></div></div></template><div v-else class="p2p-empty"><el-empty description="选择联系人开始交流" /></div></div></div></el-card></div>
      </main>
    </div>

    <el-dialog v-model="addRoomDialogVisible" title="新增教室资源" width="400px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="教室名称"><el-input v-model="newRoomForm.roomName" placeholder="如：教一201" /></el-form-item>
        <el-form-item label="容纳人数"><el-input v-model="newRoomForm.capacity" type="number" placeholder="如：60" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="addRoomDialogVisible=false">取消</el-button><el-button type="primary" @click="submitAddRoom">确定添加</el-button></template>
    </el-dialog>

    <el-dialog v-model="reserveDialogVisible" title="确认借用教室" width="400px" append-to-body>
      <p class="text-body font-medium" style="margin-bottom: var(--space-4);">您正在预约：<strong style="color:var(--primary);">{{ reserveForm.roomName }}</strong></p>
      <el-form label-position="top">
        <el-form-item label="📝 请填写使用用途 (必填)"><el-input type="textarea" v-model="reserveForm.purpose" :rows="3" placeholder="如：软件工程第一小组毕设讨论" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="reserveDialogVisible=false">取消</el-button><el-button type="success" @click="submitReserve">确认预约</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="课程全景详情" width="550px" append-to-body>
      <div v-if="currentDetail">
        <h2 class="text-h2" style="color:var(--primary); margin-top:0;">{{ currentDetail.course?.title || currentDetail.title || '未知课程' }}</h2>
        <div style="background: var(--muted); padding: var(--space-4); border-radius: var(--radius-sm); margin-top: var(--space-4);">
          <p class="text-body mt-1"><strong>⏰ 授课时间：</strong>{{ currentDetail.course?.courseTime || currentDetail.courseTime || '暂无' }}</p>
          <p class="text-body mt-2"><strong>📍 授课地点：</strong>{{ currentDetail.course?.location || currentDetail.location || '暂无' }}</p>
          <p class="text-body mt-2"><strong>📖 指定教材：</strong>{{ currentDetail.course?.textbook || currentDetail.textbook || '无指定教材或老师自带讲义' }}</p>
        </div>
        <div style="margin-top: var(--space-4);">
          <h4 class="text-h3">📝 课程内容介绍</h4>
          <p class="text-body" style="color: var(--muted-foreground); white-space: pre-wrap; line-height: 1.8;">{{ currentDetail.course?.introduction || currentDetail.introduction || '暂无介绍' }}</p>
        </div>
        <el-divider border-style="dashed" />
        <h4 class="text-h3">👨‍🏫 授课教师：{{ currentDetail.teacherName || currentDetail.teacher || '未知' }}</h4>
        <p class="text-sm mt-2"><strong>教师简介：</strong>{{ currentDetail.teacherIntro || '暂无简介' }}</p>
        <p class="text-sm mt-1"><strong>联系方式：</strong>{{ currentDetail.teacherPhone || '暂无' }}</p>
      </div>
    </el-dialog>

    <el-dialog v-model="attendanceDialogVisible" title="课程考勤打卡" width="800px" append-to-body><div style="margin-bottom: var(--space-4); display: flex; align-items: center;"><span class="text-sm font-medium">考勤日期/课次：</span><el-date-picker v-model="attendanceDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" size="small" style="width: 200px; margin-left: var(--space-2);" /></div><el-table :data="attendanceStudentList" height="400" border><el-table-column prop="userNo" label="学号" width="120" /><el-table-column prop="name" label="姓名" width="100" /><el-table-column label="考勤状态" min-width="300"><template #default="scope"><el-radio-group v-model="scope.row.attendanceStatus"><el-radio label="PRESENT">正常</el-radio><el-radio label="LATE" style="color: var(--warning);">迟到</el-radio><el-radio label="LEAVE" style="color: var(--muted-foreground);">请假</el-radio><el-radio label="ABSENT" style="color: var(--danger);">缺勤</el-radio></el-radio-group></template></el-table-column></el-table><template #footer><el-button @click="attendanceDialogVisible=false">取消</el-button><el-button type="primary" @click="submitAttendance">提交考勤</el-button></template></el-dialog>
    <el-dialog v-model="bannerDialogVisible" title="配置首页图片轮播" width="500px" append-to-body><el-form :model="bannerForm" label-width="80px"><el-form-item label="主标题"><el-input v-model="bannerForm.title" /></el-form-item><el-form-item label="副标题"><el-input v-model="bannerForm.sub" /></el-form-item><el-form-item label="背景图片"><div class="base64-upload-box" @click="triggerBannerUpload" style="height: 150px; width: 100%;"><img v-if="bannerForm.imageBase64" :src="bannerForm.imageBase64" class="preview-img" style="border-radius: var(--radius-sm);" /><div v-else class="upload-placeholder text-sm">点击选择图片</div></div><input type="file" ref="bannerFileInput" accept="image/*" style="display: none" @change="handleBannerImageSelect" /></el-form-item></el-form><template #footer><el-button @click="bannerDialogVisible=false">取消</el-button><el-button type="primary" @click="submitAdminBanner">保存配置</el-button></template></el-dialog>
    <el-dialog v-model="addUserDialogVisible" title="新增账号" width="500px" append-to-body><el-form :model="adminAddUserForm" label-width="80px"><el-form-item label="身份"><el-radio-group v-model="adminAddUserForm.role"><el-radio label="STUDENT">学生</el-radio><el-radio label="TEACHER">教师</el-radio></el-radio-group></el-form-item><el-form-item label="姓名"><el-input v-model="adminAddUserForm.name" /></el-form-item><el-form-item label="登录账号"><el-input v-model="adminAddUserForm.username" /></el-form-item><el-form-item label="初始密码"><el-input v-model="adminAddUserForm.password" type="password" show-password /></el-form-item><el-form-item :label="adminAddUserForm.role==='STUDENT'?'学号':'工号'"><el-input v-model="adminAddUserForm.userNo" /></el-form-item><el-form-item label="所属专业" v-if="adminAddUserForm.role==='STUDENT'"><el-input v-model="adminAddUserForm.major" /></el-form-item><el-form-item label="所在班级" v-if="adminAddUserForm.role==='STUDENT'"><el-input v-model="adminAddUserForm.className" /></el-form-item></el-form><template #footer><el-button @click="addUserDialogVisible=false">取消</el-button><el-button type="primary" @click="submitAdminAddUser">确认添加</el-button></template></el-dialog>
    <el-dialog v-model="evalDialogVisible" title="课程质量评估" width="600px" append-to-body><div class="eval-question"><p class="text-sm font-medium">1. 备课与熟练度</p><el-radio-group v-model="evalForm.q1"><el-radio :label="20">非常满意</el-radio><el-radio :label="16">满意</el-radio><el-radio :label="12">一般</el-radio><el-radio :label="8">不满意</el-radio><el-radio :label="4">极差</el-radio></el-radio-group></div><div class="eval-question"><p class="text-sm font-medium">2. 逻辑与重点</p><el-radio-group v-model="evalForm.q2"><el-radio :label="20">非常满意</el-radio><el-radio :label="16">满意</el-radio><el-radio :label="12">一般</el-radio><el-radio :label="8">不满意</el-radio><el-radio :label="4">极差</el-radio></el-radio-group></div><div class="eval-question"><p class="text-sm font-medium">3. 课堂氛围</p><el-radio-group v-model="evalForm.q3"><el-radio :label="20">非常满意</el-radio><el-radio :label="16">满意</el-radio><el-radio :label="12">一般</el-radio><el-radio :label="8">不满意</el-radio><el-radio :label="4">极差</el-radio></el-radio-group></div><div class="eval-question"><p class="text-sm font-medium">4. 课后辅导</p><el-radio-group v-model="evalForm.q4"><el-radio :label="20">非常满意</el-radio><el-radio :label="16">满意</el-radio><el-radio :label="12">一般</el-radio><el-radio :label="8">不满意</el-radio><el-radio :label="4">极差</el-radio></el-radio-group></div><div class="eval-question"><p class="text-sm font-medium">5. 总体收获</p><el-radio-group v-model="evalForm.q5"><el-radio :label="20">非常满意</el-radio><el-radio :label="16">满意</el-radio><el-radio :label="12">一般</el-radio><el-radio :label="8">不满意</el-radio><el-radio :label="4">极差</el-radio></el-radio-group></div><div class="eval-question" style="margin-top: var(--space-4);"><p class="text-sm font-medium">匿名改进建议</p><el-input type="textarea" v-model="evalForm.suggestion" :rows="3" placeholder="写下建议..."></el-input></div><template #footer><div style="display:flex; justify-content: space-between; align-items: center;"><span class="text-sm">总分：<strong class="text-h3">{{ totalEvalScore }}</strong></span><div><el-button @click="evalDialogVisible = false">取消</el-button><el-button type="primary" @click="submitEvaluation">提交评价</el-button></div></div></template></el-dialog>
    <el-dialog v-model="evalStatsDialogVisible" title="评教报告" width="600px" append-to-body><div v-loading="loadingStats"><div class="score-board"><div class="text-sm" style="opacity: 0.8">平均分</div><div class="text-h1">{{ evalStatsData.avgScore }}</div></div><h4 class="text-h3" style="margin-top: var(--space-6); margin-bottom: var(--space-3);">匿名反馈</h4><div v-if="evalStatsData.suggestions.length > 0" class="suggestion-list"><div v-for="(sug, index) in evalStatsData.suggestions" :key="index" class="suggestion-item text-sm"><span style="color:var(--muted-foreground)">匿名：</span> {{ sug }}</div></div><el-empty v-else description="暂无建议" /></div></el-dialog>
    
    <el-dialog v-model="addCourseDialogVisible" :title="isEditingCourse ? '修改课程' : '发布新课程'" width="600px" append-to-body><el-form :model="newCourseForm" label-width="90px"><el-form-item label="课程名称"><el-input v-model="newCourseForm.title" placeholder="如：高等数学" /></el-form-item><el-row :gutter="24"><el-col :span="12"><el-form-item label="开课学期"><el-select v-model="newCourseForm.semester"><el-option label="2025-2026春季" value="2025-2026春季"></el-option><el-option label="2025-2026秋季" value="2025-2026秋季"></el-option></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="课程类型"><el-select v-model="newCourseForm.courseType"><el-option label="必修课" value="REQUIRED"></el-option><el-option label="选修课" value="ELECTIVE"></el-option></el-select></el-form-item></el-col></el-row><el-form-item label="目标专业" v-if="newCourseForm.courseType === 'REQUIRED'"><el-input v-model="newCourseForm.targetMajor" placeholder="如：软件工程" /></el-form-item><el-form-item label="上课时间"><el-input v-model="newCourseForm.courseTime" placeholder="如：2-10周 周二 3-4节" /></el-form-item><el-form-item label="上课地点"><el-input v-model="newCourseForm.location" placeholder="如：教一201" /></el-form-item><el-form-item label="所需教材"><el-input v-model="newCourseForm.textbook" placeholder="如：同济大学第七版 高等数学" /></el-form-item><el-form-item label="课程介绍"><el-input type="textarea" v-model="newCourseForm.introduction" :rows="3" placeholder="简单介绍一下这门课的内容与考核方式..." /></el-form-item></el-form><template #footer><el-button @click="addCourseDialogVisible=false">取消</el-button><el-button type="primary" @click="submitCourseForm">立即发布</el-button></template></el-dialog>
    <el-dialog v-model="dialogVisible" :title="isGradeMode ? '录入成绩' : '学生名单'" width="800px" append-to-body><div v-if="userRole === 'TEACHER' && !isGradeMode" class="bulk-msg-box" style="margin-bottom: var(--space-4);"><h4 class="text-sm font-medium" style="margin: 0 0 var(--space-2) 0;">发通知</h4><el-input type="textarea" v-model="bulkMessageText" :rows="2"></el-input><el-button type="default" size="small" style="margin-top: var(--space-2);" @click="sendBulkMessage">发送</el-button></div><el-table :data="studentList" height="400"><el-table-column prop="userNo" label="学号" width="120" /><el-table-column prop="name" label="姓名" width="100" /><el-table-column prop="major" label="专业" width="120" /><el-table-column prop="className" label="班级" width="120" /><el-table-column label="期末成绩" width="180" align="center" v-if="isGradeMode"><template #default="scope"><div style="display:flex; gap:var(--space-2);"><el-input-number v-model="scope.row.grade" :min="0" :max="100" size="small" style="width: 100px;" /><el-button type="primary" size="small" @click="saveGrade(scope.row)">保存</el-button></div></template></el-table-column></el-table></el-dialog>
    <el-dialog v-model="profileDialogVisible" title="个人中心" width="450px" append-to-body><el-form :model="profileForm" label-width="80px" v-loading="loadingProfile"><el-form-item label="姓名"><el-input v-model="profileForm.name" /></el-form-item><el-form-item label="学/工号"><el-input v-model="profileForm.userNo" disabled /></el-form-item><el-form-item label="所属专业" v-if="userRole === 'STUDENT'"><el-input v-model="profileForm.major" /></el-form-item><el-form-item label="所在班级" v-if="userRole === 'STUDENT'"><el-input v-model="profileForm.className" /></el-form-item><el-form-item label="联系电话"><el-input v-model="profileForm.phone" /></el-form-item><el-form-item label="登录密码"><el-input v-model="profileForm.password" type="password" show-password placeholder="不修改请留空" /></el-form-item><el-form-item label="个人介绍" v-if="userRole === 'TEACHER'"><el-input type="textarea" v-model="profileForm.intro" :rows="3" /></el-form-item></el-form><template #footer><el-button @click="profileDialogVisible=false">取消</el-button><el-button type="primary" @click="submitProfileUpdate">保存</el-button></template></el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onUnmounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) { config.headers['Authorization'] = token; config.headers['token'] = token; }
  return config;
}, error => Promise.reject(error));

// 统一 API 地址：默认本地后端，部署时可通过 VITE_API_BASE_URL 覆盖
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
axios.defaults.timeout = 20000

const isLoggedIn = ref(!!localStorage.getItem('token'))
const userRole = ref(''); const userName = ref(''); const loginUsername = ref(''); const myUserId = ref(null)
const isRegisterMode = ref(false); const isResetMode = ref(false); const activeMenu = ref('home')

const parseToken = () => { try { const t = localStorage.getItem('token'); if(!t) return; const p = JSON.parse(atob(t.split('.')[1])); userRole.value = p.role.toUpperCase(); userName.value = p.name || p.username; loginUsername.value = p.username; } catch(e){} }
const registerForm = reactive({ role: 'STUDENT', username: '', password: '', name: '', userNo: '', phone: '', intro: '', major: '', className: '' })
const loginForm = reactive({ username: '', password: '' }); const isLoggingIn = ref(false); const resetForm = reactive({ username: '', phone: '', newPassword: '' }); const isResetting = ref(false)

const handleLogin = async () => {
  if(!loginForm.username){ ElMessage.warning('请输入账号'); return }
  isLoggingIn.value = true
  try {
    const res = await axios.post('/auth/login', loginForm)
    if(res.data.code===200){
      localStorage.setItem('token', res.data.data); isLoggedIn.value=true; parseToken(); activeMenu.value='home'; initData(); ElMessage.success('登录成功')
    } else ElMessage.error(res.data.message || '登录失败')
  } catch(e) { ElMessage.error('无法连接到服务器，请确认后端已启动') }
  finally { isLoggingIn.value = false }
}
const handleRegister = async () => {
  if(!registerForm.username || !registerForm.password || !registerForm.name || !registerForm.userNo){ ElMessage.warning('请填写完整的账号、密码、姓名和学工号'); return }
  try {
    const res = await axios.post('/auth/register', registerForm)
    if(res.data.code===200){ ElMessage.success('注册成功'); isRegisterMode.value=false } else ElMessage.error(res.data.message || '注册失败')
  } catch(e) { ElMessage.error('注册失败，请稍后重试') }
}
const handleResetPassword = async () => {
  if(!resetForm.username || !resetForm.phone || !resetForm.newPassword){ ElMessage.warning('请填写完整信息'); return }
  isResetting.value = true
  try {
    const res = await axios.post('/auth/resetPassword', resetForm)
    if(res.data.code===200){ ElMessage.success('重置成功'); isResetMode.value=false } else ElMessage.error(res.data.message || '重置失败')
  } catch(e) { ElMessage.error('重置失败，请稍后重试') }
  finally { isResetting.value=false }
}
const handleLogout = () => { localStorage.removeItem('token'); isLoggedIn.value=false; activeMenu.value='home'; clearChatTimer(); clearGlobalTimer(); ElMessage.success('已退出') }

const handleMenuSelect = (index) => { 
  activeMenu.value = index; if (index !== 'chat') clearChatTimer(); 
  if (index === 'hall' || index === 'home') fetchCourses(); 
  if (index === 'my' || index === 'home') loadMySchedule(); 
  if (index === 'adminCourses') loadAdminCourses(); 
  if (index === 'teacherCourses' || index === 'teacherGrades' || index === 'teacherQuality') loadMyCourses(); 
  if (index === 'chat') fetchContacts();
  if (index === 'forum') loadForumData(); 
  if (index === 'studentGrades' || index === 'studentEval') loadMyGrades();
  if (index === 'adminUsers') loadAdminUsers();
  if (index === 'adminBanners') loadAdminBanners();
  if (index === 'studentAttendance') loadMyAttendance(); 
  if (index === 'adminClassrooms') loadAdminRooms(); 
  if (index === 'reserveClassroom') loadMyReservations(); 
}
const handleCommand = (command) => { if (command === 'logout') handleLogout(); if (command === 'profile') openProfile() }

// ================= 【修复】：查空教室与预约 =================
const adminRoomList = ref([]); const loadingRooms = ref(false); const addRoomDialogVisible = ref(false); const newRoomForm = reactive({ roomName: '', capacity: '' });
const reserveDate = ref(''); const reserveSlot = ref(''); const emptyRoomList = ref([]); const myReservationList = ref([]); const reserveDialogVisible = ref(false); const reserveForm = reactive({ roomName: '', purpose: '' });

const loadAdminRooms = async () => { loadingRooms.value = true; try { const res = await axios.get('/classroom/list'); if(res.data.code === 200) adminRoomList.value = res.data.data; } catch(e){} finally { loadingRooms.value = false; } }
const openAddRoomDialog = () => { newRoomForm.roomName = ''; newRoomForm.capacity = ''; addRoomDialogVisible.value = true; }
const submitAddRoom = async () => { if(!newRoomForm.roomName || !newRoomForm.capacity) return ElMessage.warning("请填写完整信息"); try { await axios.post('/classroom/add', newRoomForm); ElMessage.success('添加成功'); addRoomDialogVisible.value = false; loadAdminRooms(); } catch(e){} }
const handleDeleteRoom = async (id) => { ElMessageBox.confirm('确定删除该物理教室吗？').then(async () => { try { await axios.post(`/classroom/delete?id=${id}`); ElMessage.success('已删除'); loadAdminRooms(); } catch(e){} }).catch(()=>{}); }

const searchEmptyRooms = async () => {
  if (!reserveDate.value || !reserveSlot.value) return ElMessage.warning('请先选择借用日期和节次！');
  loadingRooms.value = true;
  try {
    // 传给后端的一定要是唯一 ID (如: '1-2')
    const res = await axios.get(`/classroom/emptyList?date=${reserveDate.value}&slot=${reserveSlot.value}`);
    if (res.data.code === 200) emptyRoomList.value = res.data.data;
  } catch(e){} finally { loadingRooms.value = false; }
}

const openReserveDialog = (roomName) => { reserveForm.roomName = roomName; reserveForm.purpose = ''; reserveDialogVisible.value = true; }
const submitReserve = async () => {
  if(!reserveForm.purpose.trim()) return ElMessage.warning('请填写使用用途！');
  try {
    await axios.post('/classroom/reserve', {
      roomName: reserveForm.roomName,
      applicantName: userName.value,
      reserveDate: reserveDate.value,
      timeSlot: reserveSlot.value, // 使用 ID
      purpose: reserveForm.purpose
    });
    ElMessage.success('预约成功！');
    reserveDialogVisible.value = false;
    searchEmptyRooms(); 
    loadMyReservations();
  } catch(e){}
}
const loadMyReservations = async () => { try { const res = await axios.get(`/classroom/myReservations?applicantName=${userName.value}`); if(res.data.code === 200) myReservationList.value = res.data.data; } catch(e){} }

// ================= 【核心】：周次与节次智能过滤系统 =================
const selectedWeek = ref(1); // 默认选第1周
const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 增强时间段字典，涵盖新课程表里的真实数据
const timeSlots = [ 
  { id: '1-2', name: '第一大节', time: '08:00-09:35', keywords: ['上午1', '1-2节'] }, 
  { id: '3-4', name: '第二大节', time: '10:05-11:40', keywords: ['上午2', '3-4节'] }, 
  { id: '5-6', name: '第三大节', time: '14:00-15:35', keywords: ['下午1', '5-6节'] }, 
  { id: '7-8', name: '第四大节', time: '16:05-17:40', keywords: ['下午2', '7-8节'] }, 
  { id: '9-10', name: '第五大节', time: '18:30-20:05', keywords: ['晚上1', '9-10节'] } 
]

// 智能周次判断逻辑
const checkWeek = (timeStr, targetWeek) => {
  if(!timeStr) return true;
  const rangeMatch = timeStr.match(/(\d+)\s*-\s*(\d+)周/);
  if(rangeMatch) {
    const start = parseInt(rangeMatch[1]);
    const end = parseInt(rangeMatch[2]);
    return targetWeek >= start && targetWeek <= end;
  }
  const singleMatch = timeStr.match(/第(\d+)周/);
  if(singleMatch) return targetWeek === parseInt(singleMatch[1]);
  return true; // 没有周次说明，默认显示
}

// 矩阵提取逻辑
const getCourseInSlot = (day, slot, courseList) => { 
  return courseList.find(c => {
    if(!c.courseTime) return false;
    if(!c.courseTime.includes(day)) return false; // 匹配星期几
    const matchSlot = slot.keywords.some(k => c.courseTime.includes(k)); // 匹配 1-2节 等关键字
    if(!matchSlot) return false;
    return checkWeek(c.courseTime, selectedWeek.value); // 匹配周次
  });
}

// ================= 【核心修改】：安全的课程详情弹窗 =================
const detailVisible = ref(false); const currentDetail = ref(null)
const showDetail = async (id) => { 
  try{ 
    const res = await axios.get(`/course/detail?courseId=${id}`); 
    if(res.data.code === 200){ 
      currentDetail.value = res.data.data; 
      detailVisible.value = true;
    } else {
      ElMessage.error(res.data.message || '获取课程详情失败');
    }
  } catch(e) {
    ElMessage.error('无法连接到详情接口');
  } 
}

// ================= 其它系统逻辑保持稳定 =================
const attendanceDialogVisible = ref(false); const attendanceDate = ref(new Date().toISOString().split('T')[0]); const currentAttendanceCourseId = ref(null); const attendanceStudentList = ref([]); const myAttendanceList = ref([]); const loadingAttendance = ref(false);
const openAttendanceDialog = async (courseId) => { currentAttendanceCourseId.value = courseId; attendanceDate.value = new Date().toISOString().split('T')[0]; try { const res = await axios.get(`/enroll/students?courseId=${courseId}`); if (res.data.code === 200) { attendanceStudentList.value = (res.data.data || []).map(s => ({ ...s, attendanceStatus: 'PRESENT' })); attendanceDialogVisible.value = true; } } catch(e) {} }
const submitAttendance = async () => { if(!attendanceDate.value) return ElMessage.warning('请选择日期'); const records = attendanceStudentList.value.map(s => ({ courseId: currentAttendanceCourseId.value, studentId: s.studentId || s.id, recordDate: attendanceDate.value, status: s.attendanceStatus })); try { await axios.post('/enroll/submitAttendance', records); ElMessage.success('考勤录入成功'); attendanceDialogVisible.value = false; } catch(e) { ElMessage.error('失败'); } }
const loadMyAttendance = async () => { loadingAttendance.value = true; try { const res = await axios.get(`/enroll/myAttendance?studentId=${myUserId.value}`); if (res.data.code === 200) myAttendanceList.value = res.data.data || []; } catch(e) {} finally { loadingAttendance.value = false; } }

const dynamicBanners = ref([]); const adminBannerList = ref([]); const bannerDialogVisible = ref(false); const bannerFileInput = ref(null); const bannerForm = reactive({ id: null, title: '', sub: '', imageBase64: '' });
const fetchBanners = async () => { try { const res = await axios.get('/banner/list'); if(res.data.code === 200) dynamicBanners.value = res.data.data || []; } catch(e) {} }
const loadAdminBanners = async () => { try { const res = await axios.get('/banner/list'); if(res.data.code === 200) adminBannerList.value = res.data.data || []; } catch(e) {} }
const openBannerDialog = (row = null) => { if (row) { Object.assign(bannerForm, row); } else { bannerForm.id = null; bannerForm.title = ''; bannerForm.sub = ''; bannerForm.imageBase64 = ''; } bannerDialogVisible.value = true; }
const triggerBannerUpload = () => { bannerFileInput.value.click(); }
const handleBannerImageSelect = (event) => { const file = event.target.files[0]; if (!file) return; if (file.size > 2 * 1024 * 1024) return ElMessage.warning('图片请不要超过2MB'); const reader = new FileReader(); reader.readAsDataURL(file); reader.onload = () => { bannerForm.imageBase64 = reader.result; } }
const submitAdminBanner = async () => { try { await axios.post('/banner/save', bannerForm); ElMessage.success('保存成功'); bannerDialogVisible.value = false; loadAdminBanners(); fetchBanners(); } catch(e) {} }
const handleDeleteBanner = async (id) => { try { await axios.post(`/banner/delete?id=${id}`); ElMessage.success('删除成功'); loadAdminBanners(); fetchBanners(); } catch(e) {} }

const adminUserList = ref([]); const loadingUsers = ref(false); const addUserDialogVisible = ref(false);
const adminAddUserForm = reactive({ role: 'STUDENT', username: '', password: '', name: '', userNo: '', major: '', className: '' });
const loadAdminUsers = async () => { loadingUsers.value = true; try { const res = await axios.get('/admin/listUsers'); if(res.data.code === 200) adminUserList.value = res.data.data || []; } catch(e) { } finally { loadingUsers.value = false; } }
const openAddUserDialog = () => { Object.keys(adminAddUserForm).forEach(k => adminAddUserForm[k] = ''); adminAddUserForm.role = 'STUDENT'; addUserDialogVisible.value = true; }
const submitAdminAddUser = async () => { if(!adminAddUserForm.username || !adminAddUserForm.password) return ElMessage.warning("必填"); try { const res = await axios.post('/auth/register', adminAddUserForm); if(res.data.code === 200) { ElMessage.success('新增成功'); addUserDialogVisible.value = false; loadAdminUsers(); } else ElMessage.error(res.data.message); } catch(e) {} }
const handleDeleteUser = async (id) => { ElMessageBox.confirm('确定要注销该账号吗？', '警告', {type: 'error'}).then(async () => { try { await axios.post(`/admin/deleteUser?id=${id}`); ElMessage.success('已注销'); loadAdminUsers(); } catch(e) {} }).catch(()=>{}); }

const tableData = ref([]); const myScheduleList = ref([]); const adminCourseList = ref([]); const myCourseList = ref([]); const recommendList = ref([]); const loadingSchedule = ref(false); const loadingCourses = ref(false);
// 预计算未退课的课程列表，避免在课表矩阵每个单元格重复 filter
const activeMySchedule = computed(() => myScheduleList.value.filter(c => c.status != 2))
const activeMyCourses = computed(() => myCourseList.value.filter(c => c.status != 2))
const fetchCourses = async () => { try { const res = await axios.get('/course/list'); if(res.data.code===200) tableData.value = res.data.data || [] } catch(e){} }
const fetchRecommendCourses = async () => { try { const res = await axios.get('/course/recommend'); if(res.data.code===200) recommendList.value = res.data.data || [] } catch(e){} }
const loadMySchedule = async () => { loadingSchedule.value = true; try { const res = await axios.get(`/enroll/my`, {params:{username:loginUsername.value}}); if(res.data.code===200) myScheduleList.value = res.data.data || [] } catch(e){} finally { loadingSchedule.value = false } }
const handleEnroll = async (id) => { try { const res = await axios.post(`/enroll/submit`, null, {params:{username:loginUsername.value, courseId:id}}); if(res.data.code===200) {ElMessage.success('选课成功'); loadMySchedule() } else ElMessage.error(res.data.message) } catch(e){} }
const handleDrop = async (id) => { try { const res = await axios.post(`/enroll/drop`, null, {params:{username:loginUsername.value, courseId:id}}); if(res.data.code===200){ ElMessage.success('退课成功'); loadMySchedule() } } catch(e){} }
const loadAdminCourses = async () => { loadingCourses.value=true; try{ const res=await axios.get('/course/list'); if(res.data.code===200) adminCourseList.value = res.data.data || [] }catch(e){}finally{loadingCourses.value=false} }
const loadMyCourses = async () => { loadingCourses.value=true; try{ const res=await axios.get(`/course/teacherList?username=${loginUsername.value}`); if(res.data.code===200) myCourseList.value = res.data.data || [] }catch(e){}finally{loadingCourses.value=false} }
const handleComplete = async (id) => { ElMessageBox.confirm('结课后开放成绩录入通道。确认结课吗？', '提示').then(async () => { try { const res = await axios.post(`/course/complete?id=${id}`); if (res.data.code === 200) { ElMessage.success('结课成功'); loadMyCourses(); if (userRole.value === 'ADMIN') loadAdminCourses(); } } catch(e) {} }).catch(()=>{}) }

const addCourseDialogVisible = ref(false); const isEditingCourse = ref(false); const newCourseForm = reactive({ id: null, title: '', teacherId: '', credits: 2, maxCapacity: 50, courseTime: '', location: '', courseType: 'ELECTIVE', targetMajor: '', semester: '2025-2026春季', textbook: '', introduction: '' })
const openAddCourseDialog = () => { isEditingCourse.value = false; Object.keys(newCourseForm).forEach(k => newCourseForm[k] = k==='semester'?'2025-2026春季':k==='courseType'?'ELECTIVE':k==='credits'?2:k==='maxCapacity'?50:''); addCourseDialogVisible.value = true; }
const openEditCourse = (row) => { isEditingCourse.value = true; Object.assign(newCourseForm, row); addCourseDialogVisible.value = true; }
const submitCourseForm = async () => { try{ const payload = { ...newCourseForm }; if (userRole.value === 'TEACHER') payload.teacherId = myUserId.value; const url = isEditingCourse.value ? '/course/update' : '/course/add'; const res = await axios.post(url, payload); if(res.data.code===200){ ElMessage.success('操作成功'); addCourseDialogVisible.value=false; if(userRole.value === 'TEACHER') loadMyCourses(); if(userRole.value === 'ADMIN') loadAdminCourses() } }catch(e){} }
const handleDeleteCourse = async (id) => { ElMessageBox.confirm('确定删除吗？').then(async () => { try { await axios.post(`/course/delete?id=${id}`); ElMessage.success('已删除'); loadAdminCourses(); loadMyCourses() } catch(e){} }).catch(()=>{}) }

const selectedSemester = ref('ALL'); const myGradeList = ref([]); const loadingGrades = ref(false);
const studentList = ref([]); const dialogVisible = ref(false); const currentCourseIdForGrade = ref(null); const isGradeMode = ref(false);
const loadMyGrades = async () => { loadingGrades.value = true; try { const res = await axios.get(`/enroll/myGrades`, { params: { username: loginUsername.value, semester: selectedSemester.value } }); if (res.data.code === 200) myGradeList.value = res.data.data || []; } catch(e) {} finally { loadingGrades.value = false; } }
const viewStudents = async (id, isGrade) => { currentCourseIdForGrade.value = id; isGradeMode.value = isGrade; try{ const res=await axios.get(`/enroll/students?courseId=${id}`); if(res.data.code===200){ studentList.value = res.data.data || []; dialogVisible.value=true } }catch(e){} }
const saveGrade = async (row) => { if (row.grade === null || row.grade === undefined) return; try { await axios.post('/enroll/updateGrade', null, { params: { courseId: currentCourseIdForGrade.value, studentId: row.studentId, grade: row.grade } }); ElMessage.success(`已保存`); } catch(e) {} }

const evalDialogVisible = ref(false); const currentEvalCourseId = ref(null); const evalForm = reactive({ q1: 20, q2: 20, q3: 20, q4: 20, q5: 20, suggestion: '' });
const totalEvalScore = computed(() => evalForm.q1 + evalForm.q2 + evalForm.q3 + evalForm.q4 + evalForm.q5 );
const openEvalDialog = (row) => { currentEvalCourseId.value = row.courseId; evalForm.q1 = 20; evalForm.q2 = 20; evalForm.q3 = 20; evalForm.q4 = 20; evalForm.q5 = 20; evalForm.suggestion = ''; evalDialogVisible.value = true; }
const submitEvaluation = async () => { if (!evalForm.suggestion.trim()) return ElMessage.warning('请填写建议'); try { await axios.post('/enroll/evaluate', null, { params: { courseId: currentEvalCourseId.value, studentId: myUserId.value, score: totalEvalScore.value, suggestion: evalForm.suggestion.trim() } }); ElMessage.success('提交成功'); evalDialogVisible.value = false; loadMyGrades(); } catch (e) {} }
const evalStatsDialogVisible = ref(false); const loadingStats = ref(false); const evalStatsData = reactive({ avgScore: 0, suggestions: [] });
const viewEvalStats = async (course) => { evalStatsDialogVisible.value = true; loadingStats.value = true; try { const res = await axios.get(`/enroll/evalStats?courseId=${course.id}`); if(res.data.code === 200) { evalStatsData.avgScore = res.data.data.avgScore; evalStatsData.suggestions = res.data.data.suggestions || []; } } catch(e){} finally { loadingStats.value = false; } }

const forumList = ref([]); const forumSearchKeyword = ref(''); const loadingForum = ref(false); const forumDialogVisible = ref(false); const fileInput = ref(null); const newForumPost = reactive({ content: '', imageBase64: '' })
const loadForumData = async () => { loadingForum.value = true; try { const res = await axios.get(`/forum/list?keyword=${encodeURIComponent(forumSearchKeyword.value)}`); if (res.data.code === 200) { forumList.value = res.data.data || []; forumList.value.forEach(p => p.newCommentInput = '') } } catch(e) {} finally { loadingForum.value = false } }
const openPostDialog = () => { newForumPost.content = ''; newForumPost.imageBase64 = ''; forumDialogVisible.value = true }
const triggerFileInput = () => { fileInput.value.click() }
const handleImageSelect = (event) => { const file = event.target.files[0]; if (!file) return; if (file.size > 2 * 1024 * 1024) return; const reader = new FileReader(); reader.readAsDataURL(file); reader.onload = () => { newForumPost.imageBase64 = reader.result } }
const submitForumPost = async () => { if (!newForumPost.content.trim()) return; try { let postName = userName.value; if (userRole.value === 'ADMIN' && !postName.includes("管理员")) postName += " (官方)"; await axios.post('/forum/add', { authorUsername: loginUsername.value, authorName: postName, content: newForumPost.content, imageBase64: newForumPost.imageBase64 }); ElMessage.success('发布成功'); forumDialogVisible.value = false; loadForumData(); } catch(e) {} }
const handleLikePost = async (postId) => { try { await axios.post(`/forum/like?id=${postId}`); loadForumData() } catch(e) {} }
const submitComment = async (post) => { if (!post.newCommentInput.trim()) return; try { await axios.post('/forum/comment', { postId: post.id, authorUsername: loginUsername.value, authorName: userName.value, content: post.newCommentInput.trim() }); ElMessage.success('评论成功'); loadForumData() } catch(e) {} }
const handleDeletePost = async (postId) => { ElMessageBox.confirm('强制下架该贴？', '删帖').then(async () => { try { await axios.post(`/forum/delete?id=${postId}`); ElMessage.success('已清理'); loadForumData(); } catch(e) {} }).catch(()=>{}); }

const userInput = ref(''); const aiLoading = ref(false); const aiChatBox = ref(null); 
const aiChatHistory = ref([{ role: 'ai', content: '你好！我是你的专属教务 AI 助手。你可以向我咨询排课规则、推荐好课，或者任何教务系统相关的问题，直接问我吧！' }]);
const askAI = async () => { const q = userInput.value.trim(); if(!q) return; userInput.value = ''; aiChatHistory.value.push({ role: 'user', content: q }); scrollToAiBottom(); aiLoading.value = true; try { const res = await axios.get(`/ai/chat?msg=${encodeURIComponent(q)}&username=${loginUsername.value}`); aiChatHistory.value.push({ role: 'ai', content: res.data }); } catch(e) { aiChatHistory.value.push({ role: 'ai', content: '网络异常' }); } finally { aiLoading.value = false; scrollToAiBottom(); } }
const scrollToAiBottom = () => { nextTick(() => { if (aiChatBox.value) { aiChatBox.value.scrollTop = aiChatBox.value.scrollHeight; } }); }

const contactList = ref([]); const currentContact = ref(null); const currentChatHistory = ref([]); const chatMessageInput = ref(''); const chatBox = ref(null); let chatTimer = null; let globalTimer = null; const globalUnreadCount = ref(0);
const fetchGlobalUnread = async () => { if (!isLoggedIn.value || !loginUsername.value) return; try { const res = await axios.get(`/chat/unreadCount?username=${loginUsername.value}`); if (res.data.code === 200) globalUnreadCount.value = res.data.data } catch(e){} }
const fetchContacts = async () => { try { const res = await axios.get(`/chat/contacts?role=${userRole.value}`); if (res.data.code === 200) contactList.value = res.data.data || [] } catch(e) {} }
const selectContact = (contact) => { currentContact.value = contact; fetchChatHistory(); clearChatTimer(); chatTimer = setInterval(() => { fetchChatHistory(false) }, 3000) }
const fetchChatHistory = async (autoScroll = true) => { if (!currentContact.value) return; try { const res = await axios.get(`/chat/history?sender=${loginUsername.value}&receiver=${currentContact.value.username}`); if (res.data.code === 200) { const oldLen = currentChatHistory.value.length; currentChatHistory.value = res.data.data || []; if (autoScroll || (res.data.data && res.data.data.length > oldLen)) scrollToBottom(); await axios.post(`/chat/markRead?sender=${currentContact.value.username}&receiver=${loginUsername.value}`); fetchGlobalUnread(); } } catch(e) {} }
const sendP2PMessage = async () => { if (!chatMessageInput.value.trim() || !currentContact.value) return; const msgText = chatMessageInput.value.trim(); chatMessageInput.value = ''; try { await axios.post('/chat/send', { sender: loginUsername.value, receiver: currentContact.value.username, content: msgText }); fetchChatHistory(true) } catch(e) {} }
const scrollToBottom = () => { nextTick(() => { if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight }) }
const clearChatTimer = () => { if (chatTimer) { clearInterval(chatTimer); chatTimer = null } }
const clearGlobalTimer = () => { if (globalTimer) { clearInterval(globalTimer); globalTimer = null } }
onUnmounted(() => { clearChatTimer(); clearGlobalTimer(); })
const bulkMessageText = ref('');
const sendBulkMessage = async () => { if (!bulkMessageText.value.trim() || !studentList.value.length) return; for (let s of studentList.value) { try { await axios.post('/chat/send', { sender: loginUsername.value, receiver: s.username, content: bulkMessageText.value.trim() }) } catch(e) {} }; ElMessage.success('发送成功'); bulkMessageText.value = '' }

const profileDialogVisible = ref(false); const loadingProfile = ref(false); const profileForm = reactive({ id: null, username: '', name: '', userNo: '', phone: '', password: '', intro: '', major: '', className: '' })
const fetchMyInfo = async () => { try { const res = await axios.get(`/auth/getUserInfo?username=${loginUsername.value}`); if (res.data.code === 200) myUserId.value = res.data.data.id; } catch(e){} }
const openProfile = async () => { profileDialogVisible.value = true; loadingProfile.value = true; try { const res = await axios.get(`/auth/getUserInfo?username=${loginUsername.value}`); if (res.data.code === 200) { const u = res.data.data; profileForm.id = u.id; profileForm.username = u.username; profileForm.name = u.name; profileForm.userNo = u.userNo; profileForm.phone = u.phone; profileForm.intro = u.intro; profileForm.major = u.major; profileForm.className = u.className; profileForm.password = ''; } } catch(e) {} finally { loadingProfile.value = false } }
const submitProfileUpdate = async () => { try { const res = await axios.post('/auth/updateProfile', profileForm); if (res.data.code === 200) { ElMessage.success('更新成功！'); profileDialogVisible.value = false; userName.value = profileForm.name; } } catch(e) {} }

const initData = async () => { 
  await fetchMyInfo(); 
  fetchBanners(); 
  if(userRole.value==='STUDENT'){ fetchCourses(); fetchRecommendCourses(); loadMySchedule(); } 
  if(userRole.value==='ADMIN') loadAdminCourses(); 
  if(userRole.value==='TEACHER') loadMyCourses(); 
  if (userRole.value === 'STUDENT' || userRole.value === 'TEACHER') { fetchGlobalUnread(); clearGlobalTimer(); globalTimer = setInterval(fetchGlobalUnread, 3000); }
}
onMounted(() => { if(isLoggedIn.value){ parseToken(); initData(); } })
</script>

<style>
/* ==========================================
   🎨 核心设计系统 (包含最新毛玻璃效果与全局壁纸)
   ========================================== */
:root {
  --background: rgba(255, 255, 255, 0.85); 
  --foreground: #09090b;
  --muted: rgba(244, 244, 245, 0.6);
  --muted-foreground: #71717a;
  --primary: #18181b;
  --primary-foreground: #fafafa;
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --border: rgba(228, 228, 231, 0.5);
  
  --radius-sm: 4px; --radius-md: 8px; --radius-lg: 12px;
  --shadow-sm: 0 4px 12px 0 rgba(31, 38, 135, 0.05);
  --shadow-md: 0 8px 32px 0 rgba(31, 38, 135, 0.07);

  --space-1: 4px; --space-2: 8px; --space-3: 12px; --space-4: 16px;
  --space-5: 20px; --space-6: 24px; --space-8: 32px; --space-10: 40px; --space-12: 48px;
}

body { 
  margin: 0; 
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; 
  color: var(--foreground); 
  background-image: url('/login-bg.jpg'); 
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  background-repeat: no-repeat;
}

.text-h1 { font-size: 32px; line-height: 40px; font-weight: 700; letter-spacing: -0.02em; color: var(--foreground); }
.text-h2 { font-size: 24px; line-height: 32px; font-weight: 600; letter-spacing: -0.01em; color: var(--foreground); }
.text-h3 { font-size: 20px; line-height: 28px; font-weight: 600; color: var(--foreground); }
.text-body { font-size: 16px; line-height: 24px; font-weight: 400; color: var(--foreground); }
.text-sm { font-size: 14px; line-height: 20px; font-weight: 400; color: var(--muted-foreground); }
.text-xs { font-size: 12px; line-height: 16px; font-weight: 500; color: var(--muted-foreground); }
.font-medium { font-weight: 500 !important; }
.text-white { color: #ffffff !important; }
.mt-1 { margin-top: var(--space-1); }
.mt-2 { margin-top: var(--space-2); }
.line-clamp { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

/* Element-Plus 样式强行覆写 */
.el-card { border-radius: var(--radius-md) !important; border: 1px solid rgba(255, 255, 255, 0.4) !important; box-shadow: var(--shadow-sm) !important; padding: var(--space-4) !important; background: var(--background) !important; backdrop-filter: blur(12px) !important; -webkit-backdrop-filter: blur(12px) !important; }
.el-card__body { padding: 0 !important; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); }
.el-button { border-radius: var(--radius-sm) !important; font-weight: 500 !important; }
.el-button--primary { background-color: var(--primary) !important; border-color: var(--primary) !important; color: var(--primary-foreground) !important; }
.el-input__wrapper, .el-textarea__inner { border-radius: var(--radius-sm) !important; box-shadow: 0 0 0 1px var(--border) inset !important; background: rgba(255,255,255,0.5) !important; }
.el-input__wrapper.is-focus, .el-textarea__inner:focus { box-shadow: 0 0 0 1px var(--primary) inset !important; }
.el-table { --el-table-border-color: var(--border); --el-table-header-bg-color: var(--muted); background: transparent !important; }
.el-table tr, .el-table th.el-table__cell { background-color: transparent !important; }
.el-menu--horizontal { border-bottom: none !important; }
.el-dialog { border-radius: var(--radius-md) !important; background: rgba(255, 255, 255, 0.95) !important; backdrop-filter: blur(20px) !important; }

/* 登录页打通背景 */
.login-wrapper { display: flex; justify-content: center; align-items: center; height: 100vh; background: transparent; }
.login-overlay { position: absolute; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.15); z-index: 1;}
.modern-auth-card { width: 400px; padding: var(--space-6) !important; border: none !important; z-index: 2; box-shadow: var(--shadow-md) !important; }
.register-card { width: 600px; }
.auth-header { text-align: center; margin-bottom: var(--space-6); }
.auth-logo { font-size: 40px; display: inline-block; margin-bottom: var(--space-2); text-shadow: 0 2px 4px rgba(0,0,0,0.1); }

/* 主框架与毛玻璃导航栏 */
.app-layout { display: flex; flex-direction: column; min-height: 100vh; }
.main-header { display: flex; justify-content: space-between; align-items: center; padding: 0 var(--space-6); height: 64px; background: rgba(255, 255, 255, 0.75) !important; backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px); border-bottom: 1px solid rgba(255, 255, 255, 0.4); position: sticky; top: 0; z-index: 10; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.el-menu { background: transparent !important; }
.header-menu { flex: 1; margin: 0 var(--space-8); }
.main-content { padding: var(--space-8); max-width: 1400px; margin: 0 auto; width: 100%; box-sizing: border-box; display: flex; flex-direction: column; gap: var(--space-6); }

/* 轮播图与推荐 */
.dashboard-carousel { border-radius: var(--radius-lg); overflow: hidden; border: 1px solid rgba(255,255,255,0.3); }
.banner-content { height: 100%; width: 100%; position: relative; }
.banner-mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.25); display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center; padding: var(--space-6); }
.recommend-panel { display: flex; flex-direction: column; }
.recommend-scroll { flex: 1; overflow-y: auto; padding-right: var(--space-1); }
.recommend-scroll::-webkit-scrollbar { width: 4px; }
.recommend-scroll::-webkit-scrollbar-thumb { background: var(--border); border-radius: 2px; }
.recommend-mini-card { border: 1px solid var(--border); border-radius: var(--radius-sm); padding: var(--space-3); margin-bottom: var(--space-2); cursor: pointer; transition: all 0.2s ease; background: rgba(255,255,255,0.5); }
.recommend-mini-card:hover { border-color: var(--primary); box-shadow: var(--shadow-sm); background: #fff; }

/* 课表矩阵 */
.matrix-table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.matrix-table th, .matrix-table td { border: 1px solid var(--border); text-align: center; }
.matrix-table .time-th { background: rgba(255,255,255,0.3); width: 80px; padding: var(--space-2) 0; }
.matrix-table thead th { background: rgba(244,244,245,0.5); padding: var(--space-2); }
.matrix-table .time-td { background: rgba(244,244,245,0.5); padding: var(--space-1); }
.matrix-table .course-td { padding: var(--space-1); vertical-align: top; height: 90px; }
.matrix-table .course-block { border-radius: var(--radius-sm); padding: var(--space-2); text-align: left; cursor: pointer; height: 100%; box-sizing: border-box; transition: transform 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.matrix-table .course-block:hover { transform: translateY(-2px); box-shadow: var(--shadow-sm); filter: brightness(0.96); }
.student-block { background-color: rgba(236, 253, 245, 0.9); border-left: 3px solid var(--success); color: #064e3b; }
.teacher-block { background-color: rgba(254, 243, 199, 0.9); border-left: 3px solid var(--warning); color: #78350f; }

/* 论坛 */
.forum-header-bar { display: flex; justify-content: space-between; align-items: center; background: var(--background); padding: var(--space-4); border-radius: var(--radius-md); border: 1px solid var(--border); box-shadow: var(--shadow-sm); backdrop-filter: blur(12px); }
.forum-container { display: flex; flex-direction: column; gap: var(--space-4); max-width: 800px; margin: 0 auto; width: 100%; }
.forum-post-card { border-radius: var(--radius-md); }
.post-header { display: flex; align-items: center; margin-bottom: var(--space-4); }
.post-meta { margin-left: var(--space-3); }
.post-body { margin-bottom: var(--space-4); }
.post-image { max-width: 100%; max-height: 400px; border-radius: var(--radius-sm); border: 1px solid var(--border); object-fit: cover; }
.post-footer { border-top: 1px solid rgba(228,228,231,0.5); padding-top: var(--space-3); }
.comments-section { background: rgba(244,244,245,0.5); padding: var(--space-3); border-radius: var(--radius-sm); margin: var(--space-2) 0; display: flex; flex-direction: column; gap: var(--space-1); }
.comment-input-box { display: flex; align-items: center; margin-top: var(--space-3); }

/* 聊天与AI */
.ai-chat-container { background: rgba(244,244,245,0.4); border-radius: var(--radius-sm); border: 1px solid var(--border); flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.chat-history { flex: 1; padding: var(--space-6); overflow-y: auto; }
.ai-message { display: flex; align-items: flex-start; }
.message-bubble { background: #fff; padding: var(--space-3) var(--space-4); border-radius: 0 var(--radius-md) var(--radius-md) var(--radius-md); border: 1px solid var(--border); box-shadow: var(--shadow-sm); max-width: 80%; }
.chat-input-area { padding: var(--space-4); background: rgba(255,255,255,0.6); border-top: 1px solid var(--border); }

.p2p-chat-layout { display: flex; height: 100%; }
.p2p-sidebar { width: 280px; border-right: 1px solid var(--border); background: rgba(244,244,245,0.4); display: flex; flex-direction: column; }
.p2p-sidebar-header { padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--border); background: rgba(255,255,255,0.5); }
.p2p-contact-list { flex: 1; overflow-y: auto; }
.p2p-contact-item { display: flex; align-items: center; padding: var(--space-3) var(--space-5); cursor: pointer; border-bottom: 1px solid var(--border); }
.p2p-contact-item:hover { background: rgba(255,255,255,0.5); }
.active-contact { background: rgba(255,255,255,0.8); border-left: 3px solid var(--primary); }
.contact-avatar { margin-right: var(--space-3); font-weight: 600; }
.p2p-main { flex: 1; display: flex; flex-direction: column; background: transparent; }
.p2p-main-header { padding: var(--space-4) var(--space-6); border-bottom: 1px solid var(--border); background: rgba(255,255,255,0.5); }
.p2p-empty { flex: 1; display: flex; justify-content: center; align-items: center; }
.p2p-chat-window { flex: 1; padding: var(--space-6); overflow-y: auto; background: rgba(244,244,245,0.3); }
.p2p-message-row { display: flex; margin-bottom: var(--space-4); }
.p2p-msg-left { justify-content: flex-start; }
.p2p-msg-right { justify-content: flex-end; }
.p2p-avatar { margin: 0 var(--space-3); font-weight: 600; flex-shrink: 0; }
.p2p-bubble { max-width: 60%; padding: var(--space-2) var(--space-4); border-radius: var(--radius-md); border: 1px solid var(--border); box-shadow: var(--shadow-sm); }
.their-bubble { background: #fff; border-top-left-radius: 0; }
.my-bubble { background: var(--primary); color: var(--primary-foreground); border-top-right-radius: 0; }
.p2p-input-area { padding: var(--space-4); background: rgba(255,255,255,0.6); border-top: 1px solid var(--border); }

/* 工具类与弹窗内元素 */
.base64-upload-box { border: 1px dashed var(--border); border-radius: var(--radius-sm); display: flex; justify-content: center; align-items: center; cursor: pointer; background: rgba(244,244,245,0.5); transition: border-color 0.2s; }
.base64-upload-box:hover { border-color: var(--primary); }
.preview-img { width: 100%; height: 100%; object-fit: contain; }
.eval-question { background: rgba(244,244,245,0.5); padding: var(--space-4); border-radius: var(--radius-sm); margin-bottom: var(--space-3); border: 1px solid var(--border); }
.score-board { background: linear-gradient(135deg, var(--primary) 0%, #3f3f46 100%); color: white; text-align: center; padding: var(--space-6); border-radius: var(--radius-md); box-shadow: var(--shadow-md); }
.suggestion-list { max-height: 300px; overflow-y: auto; padding-right: var(--space-1); }
.suggestion-item { background: rgba(244,244,245,0.8); padding: var(--space-3); border-radius: var(--radius-sm); margin-bottom: var(--space-2); border: 1px solid var(--border); }
</style>
