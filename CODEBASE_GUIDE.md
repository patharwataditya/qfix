# QFix Codebase Guide

This document is a first-pass map of the repository for someone opening `QFix` for the first time. It focuses on:

- what each important file is responsible for
- how the main app flows are wired
- what each Java source file exposes at the function level

It is intentionally practical rather than academic. If you want to change behavior, start with the Java/UI sections, then look at the data layer and resources those classes depend on.

## 1. High-Level Architecture

QFix is a Java Android app built with XML layouts and a simple MVVM structure:

- `ui/*`: Activities, fragments, adapters, and bottom sheets
- `viewmodel/*`: state holders that expose `LiveData` and call repositories
- `data/repository/*`: app-side business logic and persistence coordination
- `data/local/*`: Room database and DAOs
- `data/model/*`: Room entities / domain objects
- `utils/*`: reusable helpers
- `res/*`: layouts, strings, icons, colors, animations, menus

The app currently runs mostly on local Room storage. The Firebase config and rules files exist, but the current code path is centered on local/demo persistence.

## 2. Main User Flows

### Citizen flow

1. `SplashActivity` decides whether to open login or a home screen.
2. `LoginActivity` authenticates via `AuthViewModel` and routes by role.
3. `HomeActivity` hosts citizen fragments via bottom navigation.
4. `HomeFragment` opens the report flow or complaint list.
5. `ReportComplaintFragment` runs a 3-step wizard:
   - `PhotosFragment`
   - `DetailsFragment`
   - `LocationFragment`
6. `MyComplaintsFragment` shows the signed-in citizen's tickets.
7. `ComplaintDetailActivity` shows ticket details.

### Authority flow

1. Authority login routes to `AuthorityDashboardActivity`.
2. `AuthorityDashboardActivity` switches between:
   - dashboard summary
   - authority complaint list
   - authority profile
3. `AuthorityDashboardFragment` shows summary cards, charts, priority queue, and recent activity.
4. `AuthorityComplaintsFragment` shows the authority's assigned/department-matching complaint list.
5. `ComplaintDetailActivity` lets an authority update ticket status through `UpdateStatusBottomSheet`.

## 3. Root-Level Files

- `README.md`: marketing-style project overview, setup notes, and high-level structure.
- `CODEBASE_GUIDE.md`: this file; detailed orientation guide.
- `LICENSE`: MIT license for the repo.
- `build.gradle`: top-level Gradle configuration shared across modules.
- `settings.gradle`: includes the `app` module and configures Gradle project settings.
- `gradle.properties`: project-wide Gradle options, including Java home selection.
- `gradlew`, `gradlew.bat`: Gradle wrapper entrypoints for Unix/Windows.
- `gradle/libs.versions.toml`: central version catalog for dependencies/plugins.
- `gradle/gradle-daemon-jvm.properties`: daemon JVM/toolchain preferences.
- `gradle/wrapper/gradle-wrapper.jar`: Gradle wrapper runtime.
- `gradle/wrapper/gradle-wrapper.properties`: Gradle wrapper version/distribution settings.
- `firebase.json`: Firebase project config used for deployable Firebase features.
- `firestore.rules`: Firestore security rules.
- `storage.rules`: Firebase Storage security rules.
- `app/google-services.json`: Firebase Android app config file.
- `app/build.gradle`: Android app module configuration, SDK versions, dependencies, plugins.
- `app/proguard-rules.pro`: custom ProGuard/R8 keep rules.

## 4. Android App Entry Files

- `app/src/main/AndroidManifest.xml`: declares permissions, activities, application class, and the `FileProvider`.
- `app/src/main/java/com/qfix/QFixApplication.java`: application entrypoint; applies the saved app theme on startup.

### `QFixApplication.java`

- `onCreate()`: global app initialization hook; currently applies theme settings through `ThemeHelper`.

## 5. Data Layer

### 5.1 Local database

- `app/src/main/java/com/qfix/data/local/AppDatabase.java`: Room database definition; exposes DAOs.
- `app/src/main/java/com/qfix/data/local/DatabaseClient.java`: singleton builder/wrapper around `AppDatabase`.

#### `AppDatabase.java`

- `complaintDao()`: DAO accessor for complaints.
- `userDao()`: DAO accessor for users.
- `feedbackDao()`: DAO accessor for feedback.
- `updateDao()`: DAO accessor for complaint updates.
- `categoryDao()`: DAO accessor for categories.

#### `DatabaseClient.java`

- `DatabaseClient(Context)`: builds the Room database instance.
- `getInstance(Context)`: singleton access point.
- `getAppDatabase()`: returns the Room DB object.

### 5.2 DAOs

- `app/src/main/java/com/qfix/data/local/dao/ComplaintDao.java`: query/update operations for complaints.
- `app/src/main/java/com/qfix/data/local/dao/UserDao.java`: query/update operations for users.
- `app/src/main/java/com/qfix/data/local/dao/FeedbackDao.java`: feedback insert/query/update operations.
- `app/src/main/java/com/qfix/data/local/dao/UpdateDao.java`: complaint timeline update insert/query operations.
- `app/src/main/java/com/qfix/data/local/dao/CategoryDao.java`: category storage access.

These files are Room interfaces, so there is no handwritten function body logic. Their method names are effectively the contract.

### 5.3 Models

- `app/src/main/java/com/qfix/data/model/User.java`: citizen/authority account record.
- `app/src/main/java/com/qfix/data/model/Complaint.java`: core complaint/ticket entity.
- `app/src/main/java/com/qfix/data/model/Feedback.java`: citizen feedback attached to a complaint.
- `app/src/main/java/com/qfix/data/model/Update.java`: authority status update/timeline entry.
- `app/src/main/java/com/qfix/data/model/Category.java`: category metadata entity.

#### `User.java`

Purpose: stores identity, profile, role, department, and work-area data.

Functions:

- `User()`: empty constructor required by Room.
- `User(String uid, String name, String email, String phone, String role)`: convenience constructor.
- `get*` / `set*` methods: plain getters/setters for each field:
  `uid`, `name`, `email`, `phone`, `role`, `profilePhotoUrl`, `address`, `ward`, `department`, `employeeId`, `password`, `createdAt`, `isVerified`, `designation`, `workArea`.

#### `Complaint.java`

Purpose: stores the complaint lifecycle, routing, assignee, status, timing, and resolution fields.

Functions:

- `Complaint()`: empty constructor.
- `get*` / `set*` methods for all complaint fields:
  `id`, `title`, `category`, `description`, `locationText`, `ward`, `photos`, `status`, `priority`, `citizenId`, `assignedDepartment`, `assignedAuthorityId`, `createdAt`, `updatedAt`, `resolvedAt`, `resolutionNote`, `resolutionPhotos`, `isPublic`, `upvotes`.

#### `Feedback.java`

Purpose: stores post-resolution citizen rating/comment.

Functions:

- `Feedback()`: empty constructor.
- `Feedback(String complaintId, String citizenId, int rating, String comment)`: convenience constructor.
- `get*` / `set*`: accessors for `id`, `complaintId`, `citizenId`, `rating`, `comment`, `createdAt`.

#### `Update.java`

Purpose: stores a timeline/status change made by an authority.

Functions:

- `Update()`: empty constructor.
- `Update(String complaintId, String authorityId, String status, String note)`: convenience constructor for a new timeline entry.
- `get*` / `set*`: accessors for `id`, `complaintId`, `authorityId`, `status`, `note`, `timestamp`.

#### `Category.java`

Purpose: stores category metadata if categories are ever made dynamic.

Functions:

- `Category()`: empty constructor.
- `Category(String id, String name, String icon)`: convenience constructor.
- `getId()`, `setId()`
- `getName()`, `setName()`
- `getIcon()`, `setIcon()`

### 5.4 Repositories

- `app/src/main/java/com/qfix/data/repository/AuthRepository.java`: local auth/session persistence and user save/load logic.
- `app/src/main/java/com/qfix/data/repository/ComplaintRepository.java`: complaint creation, routing, search/filtering, feedback, and update persistence.

#### `AuthRepository.java`

Purpose: wraps local authentication and stores current-session identity in `SharedPreferences`.

Functions:

- `AuthRepository(Context)`: repository setup.
- `signUp(email, password, name)`: creates a new user if email is unique.
- `signIn(email, password)`: validates credentials against local user table.
- `signOut()`: clears persisted session.
- `getCurrentUser()`: loads current user from stored UID.
- `sendPasswordResetEmail(email)`: placeholder success path for reset flow.
- `saveUser(user)`: persists profile edits.
- `getUser(uid)`: loads one user synchronously.
- `setCurrentUserUid(uid)`: internal helper for session persistence.

Nested type:

- `AuthResult`: small result object with:
  - `isSuccess()`
  - `getMessage()`
  - `getUser()`

#### `ComplaintRepository.java`

Purpose: central business layer for complaints, including assignment/routing rules.

Functions:

- `ComplaintRepository(Context)`: repository setup.
- `createComplaint(complaint)`: assigns ID, department, authority, then inserts.
- `updateComplaint(complaint)`: updates complaint row.
- `getComplaint(complaintId)`: gets one complaint synchronously.
- `getLocalComplaints()`: returns Room `LiveData` for all complaints.
- `getLocalComplaintsByCitizen(citizenId)`: returns citizen-scoped `LiveData`.
- `getLocalComplaintById(complaintId)`: returns complaint `LiveData`.
- `saveComplaintLocally(complaint)`: inserts one complaint.
- `saveComplaintsLocally(complaints)`: inserts many complaints.
- `getComplaintsByCitizen(citizenId)`: synchronous list for one citizen.
- `getComplaintsByWard(ward)`: ward-based filtering helper.
- `getComplaintsForAuthority(authority)`: filters tickets based on authority assignment, department, and area.
- `getComplaintsByStatus(status)`: synchronous status filter.
- `getComplaintsByPriority(priority)`: synchronous priority filter.
- `getPublicComplaints()`: current implementation returns all complaints.
- `searchComplaints(searchText)`: placeholder search implementation.
- `addFeedback(feedback)`: inserts feedback with generated ID.
- `getFeedbackForComplaint(complaintId)`: loads feedback list.
- `addUpdate(update)`: inserts timeline update with generated ID.
- `getUpdatesForComplaint(complaintId)`: loads updates list.

Internal routing helpers:

- `resolveDepartmentForCategory(category)`: maps complaint category to department.
- `findAssignedAuthorityId(department, ward)`: picks a matching authority user.
- `preferredAuthorityArea(user)`: returns work area first, ward second.
- `normalize(value)`: lowercases/trims for matching.

## 6. ViewModels

- `app/src/main/java/com/qfix/viewmodel/AuthViewModel.java`: UI-facing auth state holder.
- `app/src/main/java/com/qfix/viewmodel/ComplaintViewModel.java`: UI-facing complaint state holder.

#### `AuthViewModel.java`

Purpose: exposes auth/session state to activities/fragments through `LiveData`.

Functions:

- `AuthViewModel(Application)`: initializes repo and seeds current session state.
- `getUserLiveData()`: current user observable.
- `getIsLoadingLiveData()`: loading flag observable.
- `getErrorMessageLiveData()`: auth error observable.
- `getIsAuthenticatedLiveData()`: authenticated-state observable.
- `signUp(email, password, name)`: UI-facing signup wrapper.
- `signIn(email, password)`: UI-facing login wrapper.
- `signOut()`: clears local session state.
- `saveUser(user)`: saves edited user profile.
- `getUser(uid)`: loads one user into `userLiveData`.
- `isUserLoggedIn()`: checks for persisted session.
- `getCurrentUserId()`: convenience getter for signed-in UID.
- `getCurrentUser()`: convenience getter for the current user object.

#### `ComplaintViewModel.java`

Purpose: exposes complaint lists, single complaint, updates, feedback, loading, and errors to UI.

Functions:

- constructor and LiveData getters:
  - `ComplaintViewModel(Application)`
  - `getComplaintsLiveData()`
  - `getComplaintLiveData()`
  - `getFeedbackLiveData()`
  - `getUpdatesLiveData()`
  - `getIsLoadingLiveData()`
  - `getErrorMessageLiveData()`
- write operations:
  - `createComplaint(complaint)`
  - `updateComplaint(complaint)`
  - `addFeedback(feedback)`
  - `addUpdate(update)`
- read/filter operations:
  - `getComplaint(complaintId)`
  - `getComplaintsByCitizen(citizenId)`
  - `getComplaintsByWard(ward)`
  - `getComplaintsForAuthority(authority)`
  - `getComplaintsByStatus(status)`
  - `getPublicComplaints()`
  - `getFeedbackForComplaint(complaintId)`
  - `getUpdatesForComplaint(complaintId)`
- compatibility helpers:
  - `getComplaints()`
  - `getIsLoading()`
  - `refreshComplaints()`

## 7. UI Layer: Authentication

- `app/src/main/java/com/qfix/ui/auth/SplashActivity.java`
- `app/src/main/java/com/qfix/ui/auth/OnboardingActivity.java`
- `app/src/main/java/com/qfix/ui/auth/OnboardingFragment.java`
- `app/src/main/java/com/qfix/ui/auth/RoleSelectionActivity.java`
- `app/src/main/java/com/qfix/ui/auth/LoginActivity.java`
- `app/src/main/java/com/qfix/ui/auth/CitizenSignupActivity.java`
- `app/src/main/java/com/qfix/ui/auth/AuthoritySignupActivity.java`

#### `SplashActivity.java`

Purpose: initial launcher screen and session redirector.

Key functions:

- `onCreate()`: initializes splash logic.
- session/navigation methods route to onboarding/login/home depending on saved user state.

#### `OnboardingActivity.java`

Purpose: hosts onboarding slides and page indicators.

Functions:

- `onCreate()`: initializes views and pager.
- `initViews()`: binds layout views.
- `setupViewPager()`: creates onboarding pages.
- `setupPageIndicator()`: builds indicator dots.
- `updateIndicators(position)`: updates current-page marker.
- `setupSkipButton()`: skip/continue behavior.

Nested adapter:

- `OnboardingPagerAdapter`
  - `addFragment(fragment)`
  - `createFragment(position)`
  - `getItemCount()`

#### `OnboardingFragment.java`

Purpose: renders one onboarding page.

Functions:

- `newInstance(title, description, animation, color)`: fragment factory.
- `onCreate()`: reads arguments.
- `onCreateView()`: inflates and binds one page.

#### `RoleSelectionActivity.java`

Purpose: chooses citizen vs authority path.

Functions:

- `onCreate()`
- `initViews()`
- `setupClickListeners()`: routes to citizen signup, authority signup, or login.

#### `LoginActivity.java`

Purpose: signs users in and routes to citizen or authority home.

Functions:

- `onCreate()`
- `initViews()`
- `setupClickListeners()`
- `attemptLogin()`: validates input and signs in.
- `navigateToSignup()`
- `navigateToForgotPassword()`

#### `CitizenSignupActivity.java`

Purpose: citizen registration flow and local user creation.

Functions:

- `onCreate()`
- `initViews()`
- `setupToolbar()`
- `setupPasswordStrengthChecker()`
- `checkPasswordStrength(password)`: simple password strength UI.
- `setupClickListeners()`
- `selectProfileImage()`: currently placeholder/selection entry.
- `createAccount()`: creates citizen account and persists profile data.
- `validateForm()`: validates the signup form.

#### `AuthoritySignupActivity.java`

Purpose: authority registration flow.

Functions:

- `onCreate()`
- `initViews()`
- `setupToolbar()`
- `setupDepartmentDropdown()`: loads department options.
- `setupClickListeners()`
- `submitForVerification()`: creates authority user and routes to dashboard.
- `validateForm()`: validates authority signup fields.

## 8. UI Layer: Citizen Screens

- `app/src/main/java/com/qfix/ui/citizen/HomeActivity.java`
- `app/src/main/java/com/qfix/ui/citizen/HomeFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/ReportComplaintFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/PhotosFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/DetailsFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/LocationFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/MyComplaintsFragment.java`
- `app/src/main/java/com/qfix/ui/citizen/ProfileFragment.java`

#### `HomeActivity.java`

Purpose: citizen shell activity with bottom navigation and header.

Functions:

- `onCreate()`
- `initViews()`
- `observeUser()`: listens for current user.
- `bindUserHeader(user)`: sets greeting and city.
- `extractCity(user)`: derives city text from address/ward.
- `setupBottomNavigation()`
- `loadFragment(fragment)`: swaps the main fragment container.

#### `HomeFragment.java`

Purpose: citizen dashboard content.

Functions:

- `onCreate()`
- `onCreateView()`
- `setupQuickActions(view)`: binds report/track actions.
- `openFragment(fragment)`: replaces the citizen fragment container.

#### `ReportComplaintFragment.java`

Purpose: parent fragment for the 3-step report wizard.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupViewPager()`: sets pager and page change callback.
- `updateProgress(currentPosition)`: updates step progress UI.
- `updateStepCards(currentPosition)`: highlights active steps.
- `highlightStepCard(card)`
- `resetStepCard(card)`
- `observeViewModels()`
- `navigateToNext()`
- `navigateToPrevious()`
- `submitComplaint()`: creates and saves a complaint.

Nested `ReportPagerAdapter`:

- `createFragment(position)`
- `getItemCount()`
- `getDetailsFragment()`
- `getLocationFragment()`

#### `PhotosFragment.java`

Purpose: first step of report wizard; handles camera/gallery photo selection.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupInitialGrid()`
- `addEmptyPhotoCell()`
- `addPhotoCell(uri)`
- `showImageSourceDialog()`
- `checkCameraPermission()`
- `openCamera()`
- `openGallery()`
- `addPhoto(uri)`
- `removePhoto(uri)`
- `getPhotoUris()`

#### `DetailsFragment.java`

Purpose: second wizard step; complaint category, title, description, priority, visibility.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `initializeCategories()`: defines the available report categories.
- `setupCategories()`: inflates category chips/cards.
- `updateCategorySelection()`
- `setupClickListeners()`
- `validateInputs()`
- `getSelectedCategory()`
- `getTitle()`
- `getDescription()`
- `getPriority()`
- `isPublic()`

#### `LocationFragment.java`

Purpose: final wizard step; address/area/ward capture.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `getCurrentLocation()`: placeholder GPS hook.
- `getLocationText()`: returns formatted address summary.
- `validateInputs()`
- field getters:
  - `getNear()`
  - `getStreet()`
  - `getArea()`
  - `getCity()`
  - `getPincode()`
  - `getWard()`

#### `MyComplaintsFragment.java`

Purpose: citizen complaint list screen.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupRecyclerView()`
- `observeViewModel()`
- `showShimmer()`
- `hideShimmer()`
- `toggleEmptyState(isEmpty)`
- `onComplaintClick(complaint)`: opens detail screen.
- `loadComplaints()`: loads current user's complaints.

#### `ProfileFragment.java`

Purpose: citizen profile/settings screen.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupClickListeners(view)`
- `onViewCreated()`
- `bindUser(user)`: displays user profile data.
- `showEditProfileDialog()`
- `showLanguageSelectionDialog()`
- `getCurrentLanguageIndex()`
- `setLocale(languageCode)`
- `restartActivity()`
- `handleLogout()`
- `extractCity(user)`
- `defaultValue(value, fallback)`

## 9. UI Layer: Authority Screens

- `app/src/main/java/com/qfix/ui/authority/AuthorityDashboardActivity.java`
- `app/src/main/java/com/qfix/ui/authority/AuthorityDashboardFragment.java`
- `app/src/main/java/com/qfix/ui/authority/AuthorityComplaintsFragment.java`
- `app/src/main/java/com/qfix/ui/authority/AuthorityProfileFragment.java`
- `app/src/main/java/com/qfix/ui/authority/ComplaintsAdapter.java`

#### `AuthorityDashboardActivity.java`

Purpose: authority shell activity; swaps dashboard summary, complaint list, and profile.

Functions:

- `onCreate()`
- `initViews()`
- `bindHeader()`
- `updateHeader(user)`: shows authority identity, department, and area.
- `setupViewPager()`: creates dashboard status tabs.
- `setupBottomNavigation()`
- `showDashboard()`: shows summary pager.
- `showComplaints()`: opens the distinct complaints fragment.
- `showFragment(fragment)`: used for profile and future screens.
- `valueOrDefault(value, fallback)`

Nested `AuthorityPagerAdapter`:

- `createFragment(position)`: creates `AuthorityDashboardFragment` with a status filter.
- `getItemCount()`
- `getFilterForPosition(position)`

#### `AuthorityDashboardFragment.java`

Purpose: authority analytics/summary screen.

Functions:

- `newInstance(statusFilter)`: fragment factory.
- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupChart()`: configures MPAndroidChart bar chart.
- `observeViewModels()`
- `loadData()`: loads authority-relevant complaints.
- `updateStatistics(complaints)`: recalculates counts and UI.
- `filterComplaintsByStatus(complaints)`
- `setupChartData(complaints)`: builds weekly received/resolved data.
- `populatePriorityQueue(complaints)`
- `populateRecentActivity(complaints)`
- `addEmptyState(container, text)`
- helpers:
  - `priorityRank(priority)`
  - `priorityBadge(priority)`
  - `priorityIcon(priority)`
  - `activityTitle(complaint)`
  - `activityIcon(status)`
  - `capitalize(value)`
  - `valueOrDefault(value, fallback)`

#### `AuthorityComplaintsFragment.java`

Purpose: authority list screen with status chip filtering.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupRecyclerView()`
- `setupClickListeners()`
- `observeViewModels()`
- `loadComplaints()`: loads current authority's relevant tickets.
- `applyStatusFilter(chipId)`
- `getStatusForChip(chipId)`
- `normalizeStatus(status)`
- `showLoading()`
- `hideLoading()`

#### `AuthorityProfileFragment.java`

Purpose: authority profile/settings/stats screen.

Functions:

- `onCreate()`
- `onCreateView()`
- `initViews(view)`
- `setupClickListeners(view)`
- `onViewCreated()`
- `bindUser(user)`: populates profile header.
- `loadComplaintStats(user)`: gets authority-scoped complaints.
- `bindComplaintStats(complaints)`: shows total/resolved/pending counts.
- `showEditProfileDialog()`
- `showLanguageSelectionDialog()`
- `getCurrentLanguageIndex()`
- `setLocale(languageCode)`
- `restartActivity()`
- `handleLogout()`
- `defaultValue(value, fallback)`

#### `ComplaintsAdapter.java`

Purpose: shared RecyclerView adapter used by citizen/authority complaint lists.

Functions:

- `ComplaintsAdapter(complaints, listener)`: adapter setup.
- `onCreateViewHolder(...)`
- `onBindViewHolder(...)`
- `getItemCount()`
- `setAnimation(view, position)`: list-entry animation helper.

`ComplaintViewHolder` functions:

- constructor: binds child views and click handling.
- `bind(complaint)`: formats one complaint row.
- helpers:
  - `fallback(value, defaultValue)`
  - `capitalizeFirstLetter(text)`
  - `formatCategoryLabel(category)`
  - `getStatusColor(status)`
  - `getPriorityColor(priority)`

## 10. UI Layer: Shared Screens and Components

- `app/src/main/java/com/qfix/ui/shared/ComplaintDetailActivity.java`
- `app/src/main/java/com/qfix/ui/shared/UpdateStatusBottomSheet.java`
- `app/src/main/java/com/qfix/ui/shared/NotificationsFragment.java`
- `app/src/main/java/com/qfix/ui/shared/FeedbackFragment.java`

#### `ComplaintDetailActivity.java`

Purpose: shared detail screen for both citizen and authority users.

Functions:

- `onCreate()`
- `initViews()`
- `setupToolbar()`
- `setupActions()`: role-based bottom actions.
- `observeData()`
- `loadData()`
- `bindComplaint(complaint)`: populates the main detail UI.
- `bindAuthorityInfo(complaint)`: shows assignment information.
- `bindResolution(complaint)`: shows resolution card when resolved.
- `bindTimeline(updates)`: renders timeline entries.
- `addTimelineItem(title, subtitle)`
- `openUpdateStatusSheet()`: authority-only action.
- `applyStatusUpdate(status, comments)`: persists status change and timeline note.
- `copyTicketId()`
- `shareComplaint()`
- `shortTicketId(id)`
- `formatLabel(value, fallback)`
- `formatDate(date)`
- `valueOrDefault(value, fallback)`
- `statusColor(status)`
- `onSupportNavigateUp()`

#### `UpdateStatusBottomSheet.java`

Purpose: bottom sheet for authority-side complaint status edits.

Functions:

- `newInstance(currentStatus)`: factory.
- `setOnStatusUpdateListener(listener)`: callback registration.
- `onCreate()`
- `onCreateView()`
- `onViewCreated()`: binds dropdown and update/cancel actions.
- `formatStatus(value)`: human-readable status label.
- `normalizeStatus(value)`: converts labels back to canonical internal codes.

Interface:

- `OnStatusUpdateListener.onStatusUpdated(status, comments)`

#### `NotificationsFragment.java`

Purpose: shared notifications screen placeholder.

Key functions:

- `onCreate()`
- `onCreateView()`

#### `FeedbackFragment.java`

Purpose: feedback UI placeholder/shared fragment.

Key functions:

- `onCreate()`
- `onCreateView()`

## 11. Utilities

- `app/src/main/java/com/qfix/utils/Converters.java`: Room converters for `Date` and `List<String>`.
- `app/src/main/java/com/qfix/utils/DateUtils.java`: formatting and relative-time helpers.
- `app/src/main/java/com/qfix/utils/ImageUtils.java`: image file creation/compression helpers for photo capture.
- `app/src/main/java/com/qfix/utils/NetworkUtils.java`: connectivity check helper.
- `app/src/main/java/com/qfix/utils/ThemeHelper.java`: theme persistence and application helper.

#### `Converters.java`

- `fromTimestamp(Long)`: `Long -> Date`
- `dateToTimestamp(Date)`: `Date -> Long`
- `fromString(String)`: stored string -> `List<String>`
- `fromList(List<String>)`: list -> stored string

#### `DateUtils.java`

- `formatDate(date)`
- `formatDateTime(date)`
- `getTimeAgo(startDate, endDate)`
- `isToday(date)`
- `isYesterday(date)`

#### `ImageUtils.java`

- `compressBitmap(bitmap)`
- `resizeBitmap(bitmap)`
- `createImageFile(context)`
- `getFileUri(context, file)`
- `decodeByteArray(byteArray)`
- `saveBitmapToFile(context, bitmap, fileName)`

#### `NetworkUtils.java`

- `isNetworkAvailable(context)`

#### `ThemeHelper.java`

- `setTheme(context, theme)`
- `getTheme(context)`
- `applyTheme(context)`

## 12. Layout Files

These XML files define the app UI structure. The Java/Kotlin code above binds to them.

### Activity layouts

- `activity_splash.xml`: splash screen layout.
- `activity_onboarding.xml`: onboarding pager host.
- `activity_role_selection.xml`: citizen/authority selection screen.
- `activity_login.xml`: login screen layout.
- `activity_citizen_signup.xml`: citizen registration form.
- `activity_authority_signup.xml`: authority registration form.
- `activity_home.xml`: citizen shell with header, search, fragment container, bottom nav.
- `activity_authority_dashboard.xml`: authority shell with header, tab layout, pager, fragment container, bottom nav.
- `activity_complaint_detail.xml`: complaint detail screen used by both roles.
- `activity_main.xml`: unused/legacy placeholder activity layout.

### Citizen/authority fragment layouts

- `fragment_home.xml`: citizen dashboard body.
- `fragment_my_complaints.xml`: citizen complaints list.
- `fragment_profile.xml`: citizen profile/settings screen.
- `fragment_report_complaint.xml`: parent report wizard screen.
- `fragment_photos.xml`: photo-step layout.
- `fragment_details.xml`: category/title/description/priority step.
- `fragment_location.xml`: location/address step.
- `fragment_authority_dashboard.xml`: dashboard analytics body.
- `fragment_authority_complaints.xml`: authority complaint list.
- `fragment_authority_profile.xml`: authority profile/settings/stats.
- `fragment_notifications.xml`: notifications placeholder.
- `fragment_feedback.xml`: feedback placeholder.

### Reusable item layouts

- `complaint_item.xml`: one complaint row in RecyclerView.
- `complaint_item_shimmer.xml`: loading placeholder for complaint rows.
- `category_item.xml`: one selectable category tile.
- `priority_queue_item.xml`: one “needs attention” row on authority dashboard.
- `recent_activity_item.xml`: one activity row on authority dashboard.
- `onboarding_item.xml`: one onboarding slide.
- `photo_cell_empty.xml`: empty photo slot in the report wizard.
- `photo_cell_filled.xml`: populated photo slot in the report wizard.
- `bottom_sheet_update_status.xml`: authority status-edit sheet content.

## 13. Menus, Navigation, and XML Config

- `app/src/main/res/menu/bottom_navigation_citizen.xml`: citizen bottom-nav items.
- `app/src/main/res/menu/bottom_navigation_authority.xml`: authority bottom-nav items.
- `app/src/main/res/navigation/nav_graph.xml`: navigation graph; not all current flows use it consistently.
- `app/src/main/res/xml/backup_rules.xml`: backup behavior.
- `app/src/main/res/xml/data_extraction_rules.xml`: Android data extraction policy.
- `app/src/main/res/xml/file_paths.xml`: `FileProvider` paths for camera/photo files.

## 14. Values and Translations

- `app/src/main/res/values/strings.xml`: primary English strings.
- `app/src/main/res/values-hi/strings.xml`: Hindi translation set.
- `app/src/main/res/values/colors.xml`: app color palette.
- `app/src/main/res/values-night/colors.xml`: dark-mode color overrides.
- `app/src/main/res/values/dimens.xml`: shared spacing/sizing values.
- `app/src/main/res/values/styles.xml`: Material styles/themes/widget definitions.

## 15. Drawable, Animation, Raw, and Launcher Assets

These files are mostly visual resources. They matter for polish and identity, not business logic.

### Animations

- `anim/bounce.xml`: bounce effect.
- `anim/fade_in.xml`: fade-in effect.
- `anim/pulse.xml`: pulsing effect.
- `anim/rotate.xml`: rotation effect.
- `anim/scale_up.xml`: scale animation on taps.
- `anim/slide_in_right.xml`: RecyclerView item entry animation.

### Gradient/background drawables

- `authority_card_gradient.xml`
- `authority_signup_header_gradient.xml`
- `blue_gradient.xml`
- `citizen_card_gradient.xml`
- `login_header_gradient.xml`
- `orange_gradient.xml`
- `profile_header_gradient.xml`
- `purple_gradient.xml`
- `red_gradient.xml`
- `signup_header_gradient.xml`
- `splash_background.xml`
- `stats_card_gradient.xml`

These define branded gradients used by headers, cards, and hero areas.

### Shape/state drawables

- `circle_background.xml`: circular shape helper.
- `circular_progress_bar.xml`: circular progress appearance.
- `dashed_border.xml`: dashed outline style.
- `drag_handle.xml`: bottom-sheet handle.
- `indicator_active.xml`, `indicator_inactive.xml`: onboarding/page indicators.
- `loading_dot.xml`: loading dot shape.
- `photo_indicator_background.xml`, `photo_indicator_selector.xml`: photo pager indicators.
- `priority_chip_background.xml`, `priority_low_background.xml`, `priority_medium_background.xml`, `priority_high_background.xml`, `priority_critical_background.xml`: priority badge backgrounds.
- `status_open_background.xml`, `status_in_progress_background.xml`, `status_resolved_background.xml`, `status_rejected_background.xml`: status badge backgrounds.
- `shimmer_background.xml`: shimmer placeholder backing.

### Vector icons

`drawable/ic_*.xml` files are the icon set. Each is a small self-contained asset used by layouts/buttons/chips:

- `ic_add.xml`: generic add icon.
- `ic_add_photo.xml`: add-photo action.
- `ic_apartment.xml`: building/department icon.
- `ic_arrow_back.xml`, `ic_arrow_forward.xml`: navigation arrows.
- `ic_badge.xml`: authority badge icon.
- `ic_browse.xml`: browse/community action.
- `ic_check_circle.xml`: success/check state.
- `ic_close.xml`: dismiss/remove action.
- `ic_comment.xml`: note/comment icon.
- `ic_copy.xml`: copy/share utility.
- `ic_dashboard.xml`: dashboard tab/stat icon.
- `ic_edit.xml`: edit profile/content action.
- `ic_email.xml`: email field icon.
- `ic_emergency.xml`: emergency/fire-style action icon.
- `ic_feedback.xml`: feedback action.
- `ic_filter.xml`: filter action.
- `ic_google.xml`: Google sign-in branding asset; currently not used in login flow.
- `ic_help.xml`: help/about icon.
- `ic_home.xml`: citizen home navigation icon.
- `ic_in_progress.xml`: in-progress status icon.
- `ic_info.xml`: info/detail icon.
- `ic_language.xml`: language/settings icon.
- `ic_launcher_background.xml`, `ic_launcher_foreground.xml`: adaptive launcher layers.
- `ic_list.xml`: complaints/list navigation icon.
- `ic_location.xml`, `ic_map.xml`, `ic_pin.xml`: location/map icons.
- `ic_lock.xml`: password/security icon.
- `ic_logout.xml`: logout action.
- `ic_notifications.xml`: notifications icon.
- `ic_pending.xml`: pending/open status icon.
- `ic_person.xml`: profile/user icon.
- `ic_phone.xml`: phone field icon.
- `ic_privacy.xml`: privacy policy icon.
- `ic_refresh.xml`: refresh/report-similar action.
- `ic_rejected.xml`: rejected status icon.
- `ic_report.xml`: report issue action.
- `ic_resolved.xml`: resolved status icon.
- `ic_search.xml`: search field icon.
- `ic_share.xml`: share action.
- `ic_sort.xml`: sort action.
- `ic_star.xml`, `ic_star_outline.xml`: rating/feedback icons.
- `ic_status.xml`: status dropdown icon.
- `ic_time.xml`: time/timeline icon.
- `ic_track.xml`: track-ticket action.
- `ic_warning.xml`: warning/escalation icon.
- `ic_work.xml`: department/work icon.

### Raw assets

- `raw/splash_animation.json`: splash Lottie animation.
- `raw/bell_animation.json`: notifications-related Lottie asset.
- `raw/empty_clipboard.json`: empty-state Lottie asset.

### Launcher mipmaps

- `mipmap-*/ic_launcher.webp`
- `mipmap-*/ic_launcher_round.webp`
- `mipmap-anydpi-v26/ic_launcher.xml`
- `mipmap-anydpi-v26/ic_launcher_round.xml`

These are the generated launcher icons for different densities and adaptive launcher configs.

## 16. Tests

- `app/src/test/java/com/umera/qfix/ExampleUnitTest.java`: placeholder local unit test.
- `app/src/androidTest/java/com/umera/qfix/ExampleInstrumentedTest.java`: placeholder instrumented Android test.

These are boilerplate and currently do not validate real app behavior.

## 17. Where To Start Depending On The Change

If you want to:

- change login/signup behavior:
  start in `LoginActivity`, `CitizenSignupActivity`, `AuthoritySignupActivity`, `AuthViewModel`, `AuthRepository`
- change complaint creation:
  start in `ReportComplaintFragment`, `PhotosFragment`, `DetailsFragment`, `LocationFragment`, `ComplaintViewModel`, `ComplaintRepository`
- change category-to-department routing:
  start in `DetailsFragment` and `ComplaintRepository.resolveDepartmentForCategory()`
- change authority dashboard numbers:
  start in `AuthorityDashboardFragment`
- change authority list or filtering:
  start in `AuthorityComplaintsFragment` and `ComplaintsAdapter`
- change complaint detail or status updates:
  start in `ComplaintDetailActivity` and `UpdateStatusBottomSheet`
- change data persistence:
  start in `AppDatabase`, DAOs, models, and repository classes
- change theme/language behavior:
  start in `ThemeHelper`, `ProfileFragment`, `AuthorityProfileFragment`

## 18. Current Practical Caveats

- The app currently relies heavily on synchronous Room calls and `allowMainThreadQueries()`. That is acceptable for a demo/local prototype, but not production-safe.
- Firebase files are present, but the current runtime logic is primarily local.
- Some placeholders still exist, especially around notifications, feedback, and password reset.
- The navigation graph exists, but parts of the app use manual fragment transactions instead.

If you need a second document, the next useful one would be a “screen-to-data-flow map” that traces each button tap to the exact repository/database path it triggers.
