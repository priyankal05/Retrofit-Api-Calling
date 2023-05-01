package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BlankFragment : Fragment() {
 

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    } 
}

/*
class ForYouDataFragment : Fragment() {
    private var _binding: FragmentBlankdataBinding? = null
    private val binding get() = _binding!!
    lateinit var tinyDB: TinyDB
    private var lastPosition = 0
    private var recyclerView: FrameLayout? = null
    private var currentPage = 1
    private var currentPagePosition: Int = 0
    private var data = ArrayList<BlogItemModel>()

    companion object {
        private lateinit var adapter: CustomAdapter

        private lateinit var handler: Handler
        private lateinit var runnable: Runnable

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBlankdataBinding.inflate(inflater, container, false)
        val view = binding.root

        tinyDB = TinyDB(context)

        val gson = Gson()
        val jsonOutput: String = tinyDB.getString("getForYoudata").toString()
        val listType = object : TypeToken<List<GetBlogData>>() {}.type
        val posts: List<GetBlogData>? = gson.fromJson(jsonOutput, listType)
//        val data1 = ArrayList<BlogItemModel>()


        if (posts != null) {
            for (element in posts) {
                data.add(BlogItemModel(element))
            }

            adapter = CustomAdapter(data)
//            Log.e("getBlog", "for you: cacghing  in if " + data.size)
            binding.mainDataRecy.adapter = adapter
        } else {
            binding.progressBar.isVisible = true
            binding.txtRefresh.isVisible = false
        }

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            // Check if the user has stayed on the current page for 4 seconds
            val currentPosition = binding.mainDataRecy.currentItem
            if (currentPosition == currentPagePosition) {
                // User has stayed on the current page for 4 seconds
//                if (currentPosition != 0) {

                if (checkInternet()) {
                    if((currentPagePosition + 1) % 5 == 0) {
                        //    ads
                    } else {
                        val position1: Int = currentPagePosition  - (currentPagePosition / 5).toFloat()
                            .roundToInt()
                        getViewCountApi(data[position1].listblog.Id)
//                                Log.e("TAGs", "onCreateView: for you "+ currentPosition + "  "+ position1 +" "+ data.get(position1).listblog.title)
                    }
                }
//                }
            }
        }

        if (checkInternet()) {
            adapter = CustomAdapter(data)
            binding.mainDataRecy.setPageTransformer(DepthPageTransformer())
            binding.mainDataRecy.adapter = adapter

            getForyouBlog()
            data.clear()
            adapter.notifyDataSetChanged()

        } else {
            MainActivity.showToast("No internet connection !!", requireContext())
            binding.progressBar.isVisible = false
            binding.txtRefresh.isVisible = false
        }


        recyclerView = HomeMainFrag.recyclerView

        binding.swipeRefreshLayout.setOnRefreshListener {
//            data = ArrayList<BlogItemModel>()

//            adapter = CustomAdapter(data)
//            binding.mainDataRecy.adapter = adapter

            if (checkInternet()) {

                currentPage = 1
                getForyouBlog()
                data.clear()
                adapter.notifyDataSetChanged()
                recyclerView!!.visibility = View.VISIBLE
            }else
                binding.swipeRefreshLayout.isRefreshing = false


        }

        binding.mainDataRecy.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPagePosition = position // Update the current page position
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2500)

                if (lastPosition > position) {
//                    Log.e("TAG", "onPageSelected: page is swipe down")
                    recyclerView!!.visibility = View.VISIBLE
                } else if (lastPosition < position) {
//                    Log.e("TAG", "onPageSelected: page is swipe upp")
                    recyclerView!!.visibility = View.GONE

                    if(position >= 5){
                        if(!tinyDB.getBoolean("dialDoubletap")) {
                            dialogDoubleTapLike()
                        }
                    }

                    val count: Int = tinyDB.getInt("feedswipup1")
                    tinyDB.putInt("feedswipup1", count+1)

//                    Log.e("swipup", "feedswipup: " + count )
                    if(count >= 6) {
//                    giveFeedbackRate()
//                        Log.e("swipup", "feedswipup: give feedback dialog")
                    }

                }
                lastPosition = position

                if (position == data.size - 3) {
                    // Fetch more items and increment currentPage
                    currentPage++
                    getForyouBlog()
//                    Log.e("getBlog", "onPageSelected: FORYOU getBlogApi " + position + " last " + CategoryId)
                }
            }

        })

        if (data.isNotEmpty()) {
            binding.progressBar.isVisible = false
            binding.txtRefresh.isVisible = false
        }

        return view
    }

    private fun checkInternet():Boolean{
        val mgr = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        return if(mgr!=null){
            val netInfo = mgr.activeNetwork
            if (netInfo!=null){
                val networkConnection=mgr.getNetworkCapabilities(netInfo)
                networkConnection!=null && (networkConnection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)||networkConnection.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI))
            }else{
                false
            }
        }else{
            false
        }
    }

    fun dialogDoubleTapLike() {
        val dialswipeup: Dialog?
        dialswipeup = Dialog(requireContext())
        dialswipeup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialswipeup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialswipeup.setContentView(R.layout.dialog_double_tap)
        val consAll = dialswipeup.findViewById(R.id.cons_double) as ConstraintLayout

        consAll.setOnClickListener{
            dialswipeup.dismiss()
        }
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialswipeup.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialswipeup.show()
        dialswipeup.window?.attributes = lp
        tinyDB.putBoolean("dialDoubletap", true)
    }


    private fun getForyouBlog() {

        val authToken = tinyDB.getString("auth_token")

//        Log.e("getBlog", "getallblog :FOR YOU catid currentPage " + currentPage)

        val apiInterface =
            RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

        val userCall: Call<List<GetBlogData>> = apiInterface.getForyouBlogData(currentPage, 9)

        // ArrayList of class ItemsViewModel
//        val data = ArrayList<BlogItemModel>()


        userCall.enqueue(object : Callback<List<GetBlogData>> {
            override fun onResponse(
                call: Call<List<GetBlogData>>,
                response: Response<List<GetBlogData>>
            ) {

                val otpdata: List<GetBlogData>? = response.body()
                if (response.isSuccessful) {
                    if (otpdata != null) {
                        try {
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.progressBar.isVisible = false
                            binding.txtRefresh.isVisible = false

                        }catch (_:Exception){}

                        for (element in otpdata) {
                            data.add(BlogItemModel(element))
                        }
//                       adapter = CustomAdapter(data)

//                        Log.e("getBlog", "for you: size " + otpdata.size +" " +data.size )

                        val gson = Gson()
                        val json = gson.toJson(otpdata)
                        tinyDB.putString("getForyoudata", json)
                        adapter.notifyDataSetChanged()

                    }
                } else {
                    Log.e("getBlog", "onResponse: else for you $otpdata")
                }
            }

            override fun onFailure(call: Call<List<GetBlogData>>, t: Throwable) {
                Log.e("getBlog", "home onFailure by for you " + t.message.toString() + " " + call)

            }
        })
    }




    class CustomAdapter(mList1:  ArrayList<BlogItemModel>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        lateinit var tinydb: TinyDB

        private lateinit var bookmarklist: ArrayList<String>
        private lateinit var likelist: ArrayList<String>
        private var lastTapTime: Long = 0
        private val doubleTapInterval: Long = 300 // in milliseconds

        private lateinit var animlike: ValueAnimator
        private lateinit var animlikecb: ValueAnimator
        var mList  = mList1


        private val ADS = 1
        val DATA = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
              // inflates the card_view_design view
              // that is used to hold list item
              val view =
                  LayoutInflater.from(parent.context).inflate(R.layout.maindata_layout, parent, false)
              tinydb = TinyDB(parent.context)

              bookmarklist = tinydb.getListString("userBookmarkid")
              likelist = tinydb.getListString("userLikeid")

  //            animation = AnimationUtils.loadAnimation(parent.context, R.anim.bounce)
              animlike = ValueAnimator.ofFloat(1f, 1.4f, 1f)
              animlike.duration = 400


              animlikecb = ValueAnimator.ofFloat(0f, 1.2f, 1f)
              animlikecb.duration = 400

              return ViewHolder(view)
          }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var viewHolder: RecyclerView.ViewHolder? = null
            val inflater = LayoutInflater.from(parent.context)
            tinydb = TinyDB(parent.context)

            bookmarklist = tinydb.getListString("userBookmarkid")
            likelist = tinydb.getListString("userLikeid")

            animlike = ValueAnimator.ofFloat(1f, 1.4f, 1f)
            animlike.duration = 400

            animlikecb = ValueAnimator.ofFloat(0f, 1.2f, 1f)
            animlikecb.duration = 400

            when (viewType) {
                DATA -> {
                    val v2: View = inflater.inflate(R.layout.maindata_layout, parent, false)
                    viewHolder = ViewHolder(v2)
                }
                ADS -> {
                    val v1: View = inflater.inflate(R.layout.ad_content, parent, false)
                    viewHolder = AdsHolder(v1)
                }
//                else -> {}
            }
            assert(viewHolder != null)
            return viewHolder!!
        }
 
        // binds the list items to a view
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holders: RecyclerView.ViewHolder, position: Int) {

            when (holders.itemViewType) {
                ADS -> {
                    val adHolder = holders as AdsHolder

                    val nativeAd = NativeAdSingleton.getNativeAd()
                    if (nativeAd != null) {
                        val nativeContentAdView = (holders.itemView.context as Activity).layoutInflater.inflate(R.layout.ad_content, null) as NativeAdView
                        adHolder.mRelativeFBAdmob.visibility = View.VISIBLE
                        populateContentAdView(nativeAd, nativeContentAdView)
                        adHolder.mRelativeFBAdmob.removeAllViews()
                        adHolder.mRelativeFBAdmob.addView(nativeContentAdView)
                    }
 
                }
                DATA -> {
                    val aposition: Int = position - (position / 5).toFloat().roundToInt()
                    val holder:  ViewHolder = holders as  ViewHolder
                    tinydb = TinyDB(holder.itemView.context)

                    val ItemsViewModel = mList[aposition]

                    var parsedIntlike = ItemsViewModel.listblog.likeCount.toString().toInt()

//            holder.imageView.setImageResource(ItemsViewModel.image)

                    holder.txtTitlemain.text = ItemsViewModel.listblog.title
                    holder.txtViews.text = ItemsViewModel.listblog.viewCount.toString() + " Reads"
//                  Log.e("TAGs", "Views  " + ItemsViewModel.listblog.viewCount.toString())
                    holder.txtlikesID.text = ItemsViewModel.listblog.likeCount.toString() + " Likes"
                    holder.txtPub.text = " " + ItemsViewModel.listblog.publisher.toString()
                    if (ItemsViewModel.listblog.tagId.isNotEmpty()) {
                        holder.txtTag.text = "# " + ItemsViewModel.listblog.tagId[0].title.toString()
                    }


//        Log.e("TAG", "time: " + userModal.getBlog_createdTime());
                    val creDatime: String = ItemsViewModel.listblog.createdAt!!.substring(0, 10) + " " + ItemsViewModel.listblog.createdAt!!.substring(11, 19)
//                    Log.e("date", "creDatime $creDatime \n real utc " + ItemsViewModel.listblog.createdAt)

                    val frDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val frtime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    val curDatime = "$frDate $frtime"

//                    Log.e("date", "curDatime $curDatime")
                    val blogtime = findDifference(creDatime, curDatime)
//                    Log.e("date", "blogtime after findDifference $blogtime")
                    holder.txtTimeAgo.text = " / $blogtime"


                    val options: RequestOptions =
                        RequestOptions().centerInside()
                            .error(R.drawable.ic_loading_full)

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        holder.ivmain.setRenderEffect(RenderEffect.createBlurEffect(20.0f, 20.0f, Shader.TileMode.CLAMP))
                    }


                    if (ItemsViewModel.listblog.smallSizeImage != null) {

                        Glide.with(holder.itemView.context)
                            .load(AppstartActivity.ImageUrl + ItemsViewModel.listblog.originalSizeImage!!.url)
                            .thumbnail(Glide.with(holder.itemView.context).load(AppstartActivity.ImageUrl + ItemsViewModel.listblog.smallSizeImage!!.url))
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .listener(object : RequestListener<Drawable> {

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                           
                                    Glide.with(holder.itemView.context).load(resource)
                                        .override(6, 6)
                                        .into(holder.ivmain)

                                    return false
                                }

                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                                    Log.e("TAGi", "onLoadFailed: iamge not ")
                                    return false
                                }
                            }).apply(options)
                            .into(holder.ivmainReal)
                    }
 

                    holder.ivmain.setOnClickListener {
                        if (System.currentTimeMillis() - lastTapTime < doubleTapInterval) {
                            // Handle double tap
                            holder.ivLikeAnim.isVisible = true
                            // 3
                            animlike.addUpdateListener {
                                val value = it.animatedValue as Float
                                holder.ivLikeAnim.scaleX = value
                                holder.ivLikeAnim.scaleY = value
                            }
                            animlike.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    // animation is finished
                                    val anim = ValueAnimator.ofFloat(1f, 1f, 0f)
                                    anim.duration = 300
                                    anim.addUpdateListener {
                                        val value = it.animatedValue as Float
                                        holder.ivLikeAnim.scaleX = value
                                        holder.ivLikeAnim.scaleY = value
                                    }
                                    anim.start()
                                }
                            })
                            animlike.start()

                            val isCheckedPsw: Boolean = holder.ivlike.isChecked

                            if (isCheckedPsw) {
                                //if select
//                        getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)
//                        parsedInt++
//                        holder.txtlikesID.text = parsedInt.toString() + " Likes"
                            } else {
                                holder.ivlike.isChecked = true
//                        MainActivity.showToast("liked in else ", holder.itemView.context)

                                val count: Int
                                count = tinydb.getInt("feedlike")

                                tinydb.putInt("feedlike", count+1 )
                                parsedIntlike++
                                holder.txtlikesID.text = "$parsedIntlike Likes"
                                getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)

                            }

                            animlikecb.addUpdateListener {
                                val value = it.animatedValue as Float
                                holder.ivlike.scaleX = value
                                holder.ivlike.scaleY = value
                            }
                            animlikecb.start()

                        }
                        lastTapTime = System.currentTimeMillis()
                    }

//                    Log.e("TAG", "onBindViewHolder: bookmarklist " + bookmarklist)

                    // bookmark
                    holder.ivbookmark.setOnClickListener {
                        val isCheckedPsw: Boolean = holder.ivbookmark.isChecked
//                        Log.e("getBook", "setOnClickListener book: " )

                        if (isCheckedPsw) {
//                            Log.e("getBook", "onBindViewHolder: if  " )
                            //if select
                            getBookmarkApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)
                        } else {
                            getBookmarkApi(ItemsViewModel.listblog.Id, holder.itemView.context, 1)
//                            Log.e("getBook", "onBindViewHolder: else " )
                        }
                    }
                    holder.rvbookmark.setOnClickListener {
                        val isCheckedPsw: Boolean = holder.ivbookmark.isChecked
//                        Log.e("getBook", "setOnClickListener book: $isCheckedPsw")

                        if (isCheckedPsw) {
//                            Log.e("getBook", "onBindViewHolder: if ")
                            //if select
                            getBookmarkApi(ItemsViewModel.listblog.Id, holder.itemView.context, 1)
                            holder.ivbookmark.isChecked = false
                        } else {
                            getBookmarkApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)
//                            Log.e("getBook", "onBindViewHolder: else ")
                            holder.ivbookmark.isChecked = true
                        }
                    }


//            var databookm = tinydb.getListString("bookmarksave")
//            Log.e("getBook", " save" + databookm + " " + ItemsViewModel.listblog.Id)

                    for (i in 0 until bookmarklist.size) {
                        if (bookmarklist[i] == ItemsViewModel.listblog.Id) {
                            holder.ivbookmark.isChecked = true
//                            Log.e("getBook", "id is same: save")
                            break
                        }else{
                            holder.ivbookmark.isChecked = false
                        }
                    }


                    // like
                    holder.ivlike.setOnClickListener {

                        animlikecb.addUpdateListener {
                            val value = it.animatedValue as Float
                            holder.ivlike.scaleX = value
                            holder.ivlike.scaleY = value
                        }
                        animlikecb.start()

                        val isCheckedPsw: Boolean = holder.ivlike.isChecked
                        Log.e("getlike", "setOnClickListener book: $isCheckedPsw")

                        if (isCheckedPsw) {
                            //if select
                            getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)
                            parsedIntlike++
                            holder.txtlikesID.text = "$parsedIntlike Likes"
                            ItemsViewModel.listblog.likeCount = parsedIntlike
                        } else {
                            getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 1)
                            parsedIntlike--
                            holder.txtlikesID.text = "$parsedIntlike Likes"
                            ItemsViewModel.listblog.likeCount = parsedIntlike
                        }
                    }
                    holder.rvlike.setOnClickListener {

                        animlikecb.addUpdateListener {
                            val value = it.animatedValue as Float
                            holder.ivlike.scaleX = value
                            holder.ivlike.scaleY = value
                        }
                        animlikecb.start()

                        val isCheckedPsw: Boolean = holder.ivlike.isChecked
                        Log.e("getlike", "setOnClickListener book: $isCheckedPsw")

                        if (isCheckedPsw) {
                            //if select
                            getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 1)
                            parsedIntlike--
                            holder.txtlikesID.text = "$parsedIntlike Likes"
                            holder.ivlike.isChecked = false
                            ItemsViewModel.listblog.likeCount = parsedIntlike
                        } else {
                            getLikedApi(ItemsViewModel.listblog.Id, holder.itemView.context, 0)
                            parsedIntlike++
                            holder.txtlikesID.text = "$parsedIntlike Likes"
                            holder.ivlike.isChecked = true
                            ItemsViewModel.listblog.likeCount = parsedIntlike
                        }
                    }


//            var datalike = tinydb.getListString("likebloglist")
//            Log.e("getlike", " like " + datalike + " " + ItemsViewModel.listblog.Id)

                    for (i in 0 until likelist.size) {
                        if (likelist[i] == ItemsViewModel.listblog.Id) {
                            holder.ivlike.isChecked = true
                            Log.e("getlike", "id is same: like")
                            break
                        } else{
                            holder.ivlike.isChecked = false
                        }
                    }

                    holder.txtTitlemain.setOnClickListener {

                        holder.itemView.context.startActivity(
                            Intent(holder.itemView.context, WebViewActivity::class.java).apply {
                                putExtra("url", ItemsViewModel.listblog.deepLink.toString())
                                putExtra("auto" , ItemsViewModel.listblog.auto)
                                putExtra("title", holder.itemView.context.resources.getString(R.string.news))
                            })
                        clickCount(ItemsViewModel.listblog.Id)


                    }

                    holder.relaPub.setOnClickListener {

                        holder.itemView.context.startActivity(
                            Intent(holder.itemView.context, WebViewActivity::class.java).apply {
                                putExtra("url", ItemsViewModel.listblog.deepLink.toString())
                                putExtra("auto" , ItemsViewModel.listblog.auto)
                                putExtra("title", holder.itemView.context.resources.getString(R.string.news))
                            })
                        clickCount(ItemsViewModel.listblog.Id)
                    }


                    holder.imghare.setOnClickListener {
//                parsedIntlike = ItemsViewModel.listblog.likeCount.toString().toInt()
 


                        val imgurl =
                            AppstartActivity.ImageUrl + ItemsViewModel.listblog.mediumSizeImage!!.url
//                var imgurl =   "https://onesec.sgp1.digitaloceanspaces.com/images/1676615846232_63ef20a6387ce2470ee7adc6_aaa.jpg"

                        generateSharingLink(
                            deepLink = "${Constants.PREFIX}/post/${ItemsViewModel.listblog.Id}".toUri(),
                            previewImageLink = imgurl.toUri(),
                            previewTitle1 = ItemsViewModel.listblog.title.toString()
                        ) { generatedLink ->
                            holder.itemView.context.shareDeepLink(
                                deepLink = generatedLink
                            )
                        }
                    }

                    holder.imgReport.setOnClickListener {
//                        ReportDialog(holder.itemView.context, ItemsViewModel.listblog.Id.toString())
                        reportDialog( holder.itemView.context, ItemsViewModel.listblog.Id.toString(), aposition , mList)

//                val bottomSheet = BottomSheetDialog(holder.itemView.context)
//                val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
//                bottomSheet.show((holder.itemView.context as Activity).fragmentManager, "ModalBottomSheet")
                    }
                }

            }

        }

//        @SuppressLint("WrongConstant")

        private fun populateContentAdView(
            nativeContentAd: NativeAd,
            nativeContentAdView: NativeAdView
        ) {

//            ivAds.visibility = View.GONE

            nativeContentAdView.mediaView = nativeContentAdView.findViewById(R.id.ad_media)
            nativeContentAdView.headlineView = nativeContentAdView.findViewById(R.id.ad_headline)
            nativeContentAdView.bodyView = nativeContentAdView.findViewById(R.id.ad_body)
            nativeContentAdView.callToActionView = nativeContentAdView.findViewById(R.id.ad_call_to_action)
            nativeContentAdView.iconView = nativeContentAdView.findViewById(R.id.ad_app_icon)
            nativeContentAdView.storeView = nativeContentAdView.findViewById(R.id.ad_store)
            nativeContentAdView.starRatingView = nativeContentAdView.findViewById(R.id.ad_stars)
            nativeContentAdView.starRatingView = nativeContentAdView.findViewById(R.id.ad_stars)

            nativeContentAdView.mediaView!!.mediaContent = nativeContentAd.mediaContent
            (nativeContentAdView.headlineView as TextView?)!!.text = nativeContentAd.headline

            if (nativeContentAd.body == null) {
                nativeContentAdView.bodyView!!.visibility = View.INVISIBLE
            } else {
                nativeContentAdView.bodyView!!.visibility = View.VISIBLE
                (nativeContentAdView.bodyView as TextView?)!!.text = nativeContentAd.body
            }
            if (nativeContentAd.callToAction == null) {
                nativeContentAdView.callToActionView!!.visibility = View.INVISIBLE
            } else {
                nativeContentAdView.callToActionView!!.visibility = View.VISIBLE
                (nativeContentAdView.callToActionView as Button?)!!.text =
                    nativeContentAd.callToAction
            }
            if (nativeContentAd.icon == null) {
                nativeContentAdView.iconView!!.visibility = View.GONE
            } else {
                (nativeContentAdView.iconView as ImageView?)!!.setImageDrawable(
                    nativeContentAd.icon!!.drawable
                )
                nativeContentAdView.iconView!!.visibility = View.VISIBLE
            }
            if (nativeContentAd.store == null) {
                nativeContentAdView.storeView!!.visibility = View.INVISIBLE
            } else {
                nativeContentAdView.storeView!!.visibility = View.VISIBLE
                (nativeContentAdView.storeView as TextView?)!!.text = nativeContentAd.store
            }
            if (nativeContentAd.starRating == null) {
                nativeContentAdView.starRatingView!!.visibility = View.INVISIBLE
            } else {
                nativeContentAdView.starRatingView!!.visibility = View.VISIBLE
                nativeContentAdView.starRatingView!!.visibility = View.VISIBLE
            }

            nativeContentAdView.setNativeAd(nativeContentAd)
        }

      


        private fun reportDialog(context: Context, reportblogid: String, position: Int, mList:  ArrayList<BlogItemModel> ) {
            val dialog = BottomSheetDialog(context, R.style.MaterialDialogSheet)
//            val dialog = BottomSheetDialog(context )

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_report, null, false)
            val btnnotInterest = view.findViewById<TextView>(R.id.btnnotInterest)
            val btnReport = view.findViewById<TextView>(R.id.btnReport)


            btnnotInterest.setOnClickListener {

                if (checkInternet(context)) {

                    notinteresApi(reportblogid, context)
                    dialog.dismiss()
                    //                      val mutableList = data.toMutableList()
//                      mutableList.removeAt(itemToRemoveIndex) // Remove the item from the ArrayList
                    mList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, mList.size)
                    adapter.notifyDataSetChanged() // Notify the adapter that the data has changed
                } else {
                    MainActivity.showToast("Please check your internet connection !!", context)
                    dialog.dismiss()
                }

            }

            btnReport.setOnClickListener {
                if (checkInternet(context)) {
                    dialog.dismiss()
                    dialogmoreReport(context, reportblogid)
                } else {
                    MainActivity.showToast("Please check your internet connection !!", context)
                    dialog.dismiss()
                }


            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        private fun checkInternet(context: Context):Boolean {
            val mgr = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
            return if(mgr!=null){
                val netInfo = mgr.activeNetwork
                if (netInfo!=null){
                    val networkConnection=mgr.getNetworkCapabilities(netInfo)
                    networkConnection!=null && (networkConnection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)||networkConnection.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI))
                }else{
                    false
                }
            }else{
                false
            }
        }

        private fun dialogmoreReport(context: Context, repoBlogid: String) {
            val dialog =BottomSheetDialog(context, R.style.MaterialDialogSheet)

            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_more_report, null, false)
            val btnReport1 = view.findViewById<TextView>(R.id.btnReport1)
            val btnReport2 = view.findViewById<TextView>(R.id.btnReport2)
            val btnReport3 = view.findViewById<TextView>(R.id.btnReport3)
            val btnReport4 = view.findViewById<TextView>(R.id.btnReport4)
            val btnReport5 = view.findViewById<TextView>(R.id.btnReport5)
            val btnReport6 = view.findViewById<TextView>(R.id.btnReport6)

            btnReport1.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport1.text.toString(), context)
                dialog.dismiss()
            }
            btnReport2.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport2.text.toString(), context)
                dialog.dismiss()
            }
            btnReport3.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport3.text.toString(), context)
                dialog.dismiss()
            }
            btnReport4.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport4.text.toString(), context)
                dialog.dismiss()
            }
            btnReport5.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport5.text.toString(), context)
                dialog.dismiss()
            }
            btnReport6.setOnClickListener {
                reportBlogApi(repoBlogid, btnReport6.text.toString(), context)
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        private fun notinteresApi(reportid: String, context: Context) {

            val authToken = tinydb.getString("auth_token")

            val apiInterface =
                RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

            val paramObject = JSONObject()
            paramObject.put("bookmarkedBlog", reportid)

            val userCall: Call<NotInterestBlog> = apiInterface.notInterested(paramObject.toString())
            userCall.enqueue(object : Callback<NotInterestBlog> {
                override fun onResponse(
                    call: Call<NotInterestBlog>,
                    response: Response<NotInterestBlog>
                ) {
                    val otpdata = response.body()
                    if (response.isSuccessful) {
                        if (otpdata != null) {

                            Log.e("getNointer", "onRespo not intere" + otpdata.Id)
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        try {
                            MainActivity.showToast(
                                "  " + jObjError.getJSONArray("details").getJSONObject(0)
                                    .getString("message"), context
                            )

                        } catch (e: Exception) {
                            try {
                                MainActivity.showToast(
                                    "  " + jObjError.getString("message"),
                                    context
                                )

                            } catch (e: Exception) {
                                MainActivity.showToast(
                                    "Something went wrong in not interest",
                                    context
                                )

                            }
                        }
                    }

                }

                override fun onFailure(call: Call<NotInterestBlog>, t: Throwable) {
                    Log.e("getNointer", "onFailure: $t\n$call")
//                    MainActivity.showToast("onFailure " + t.toString(), context)
                }
            })


        }

        private fun reportBlogApi(blogid: String, repoTitle: String, context: Context) {

            val authToken = tinydb.getString("auth_token")

            val apiInterface =
                RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

            val paramObject = JSONObject()
            paramObject.put("reportTitle", repoTitle)
            paramObject.put("blogId", blogid)

            val userCall: Call<NotInterestBlog> = apiInterface.ReportedBlog(paramObject.toString())
            userCall.enqueue(object : Callback<NotInterestBlog> {
                override fun onResponse(
                    call: Call<NotInterestBlog>,
                    response: Response<NotInterestBlog>
                ) {
                    val otpdata = response.body()
                    if (response.isSuccessful) {
                        if (otpdata != null) {

                            MainActivity.showToast("Thanks for reporting", context)

                            Log.e("getReport", "onRespo report" + otpdata.Id)
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        try {
                            MainActivity.showToast(
                                "  " + jObjError.getJSONArray("details").getJSONObject(0)
                                    .getString("message"), context
                            )
                        } catch (e: Exception) {
                            try {
                                MainActivity.showToast(
                                    "  " + jObjError.getString("message"),
                                    context
                                )
                            } catch (e: Exception) {
                                MainActivity.showToast(
                                    "Something went wrong in not interest",
                                    context
                                )
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<NotInterestBlog>, t: Throwable) {
                    Log.e("getNointer", "onFailure: $t\n$call")
//                    MainActivity.showToast("onFailure " + t.toString(), context)

                }
            })
        }

        private fun getBookmarkApi(bookmarkedBlog: String?, context: Context, i: Int) {

            val data: ArrayList<String> = tinydb.getListString("userBookmarkid")


            val authToken = tinydb.getString("auth_token")

            val apiInterface =
                RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

            val paramObject = JSONObject()

            paramObject.put("bookmarkedBlog", bookmarkedBlog)

            val userCall: Call<GetUserData> = apiInterface.getBookmark(paramObject.toString())

            userCall.enqueue(object : Callback<GetUserData> {
                override fun onResponse(call: Call<GetUserData>, response: Response<GetUserData>) {
                    val otpdata = response.body()
                    if (response.isSuccessful) {
                        if (otpdata != null) {

                            val gson = Gson()
                            val json = gson.toJson(otpdata)
                            tinydb.putString("getuserdata", json)
                            if (i == 0) {
                                data.add(bookmarkedBlog.toString())

                                MainActivity.showToast("Saved to profile ", context)

                            } else {
                                data.remove(bookmarkedBlog.toString())
                            }
                            Log.e("getbookmark", "onRespo arraylist of save$data")
                            tinydb.putListString("userBookmarkid", data)
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        try {
                            MainActivity.showToast(
                                "  " + jObjError.getJSONArray("details").getJSONObject(0)
                                    .getString("message"), context
                            )

                        } catch (e: Exception) {
                            try {
                                MainActivity.showToast(
                                    "  " + jObjError.getString("message"),
                                    context
                                )
                            } catch (e: Exception) {
                                MainActivity.showToast("Something went wrong in bookmark", context)
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<GetUserData>, t: Throwable) {
                    Log.e("getbookmark", "onFailure: $t\n$call")
//                    MainActivity.showToast("onFailure " + t.toString(), context)

                }
            })

        }

        private fun getLikedApi(likeblogID: String?, context: Context, i: Int) {

            if (i == 0) {
                val count = tinydb.getInt("feedlike")

                Log.e("like", "getLikedApi: $count")
//                if(count >= 6) {
////                    DialogRatexperience(context)
////                    Log.e("TAG", "getLikedApi: give feedback dialog")
//                }
            }
            val data: ArrayList<String> = tinydb.getListString("userLikeid")


            val authToken = tinydb.getString("auth_token")

            val apiInterface = RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

            val paramObject = JSONObject()

            paramObject.put("likedBlog", likeblogID)

            val userCall: Call<LikeModel> = apiInterface.getLikeblog(paramObject.toString())

            userCall.enqueue(object : Callback<LikeModel> {
                override fun onResponse(call: Call<LikeModel>, response: Response<LikeModel>) {
                    val otpdata = response.body()
                    if (response.isSuccessful) {
                        if (otpdata != null) {

//                            val gson = Gson()
//                            val json = gson.toJson(otpdata)
//                            tinydb.putString("getuserdata", json)
                            if (i == 0) {
                                data.add(likeblogID.toString())
//                                MainActivity.showToast("Liked", context)

                            } else {
                                data.remove(likeblogID.toString())
//                                MainActivity.showToast("Unliked!!", context)
                            }
                            Log.e("getlike", "onRespo arraylist of save$data")
                            tinydb.putListString("userLikeid", data)
                        }
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        try {
                            MainActivity.showToast(
                                "  " + jObjError.getJSONArray("details").getJSONObject(0)
                                    .getString("message"), context
                            )
                        } catch (e: Exception) {
                            try {
                                MainActivity.showToast(
                                    "  " + jObjError.getString("message"),
                                    context
                                )
                            } catch (e: Exception) {
                                MainActivity.showToast("Something went wrong in like", context)

                            }
                        }
                    }

                }

                override fun onFailure(call: Call<LikeModel>, t: Throwable) {
                    Log.e("getlike", "onFailure: $t\n$call")
//                    MainActivity.showToast("onFailure " + t.toString(), context)
                }
            })

        }

        private fun clickCount(Blogid: String?) {

            val authToken = tinydb.getString("auth_token")

            val apiInterface =
                RetrofitHelperAuth.getInstance(authToken).create(ApiInterface::class.java)

            val userCall: Call<ClickCount> = apiInterface.blogClickCount(Blogid.toString())

            userCall.enqueue(object : Callback<ClickCount> {
                override fun onResponse(call: Call<ClickCount>, response: Response<ClickCount>) {
                    val otpdata = response.body()
                    if (response.isSuccessful) {
                        if (otpdata != null) {

                            Log.e(
                                "getCount",
                                "onRespo  clickcount user size " + otpdata.clickBlogUserId.size
                            )

                        }
                    } else {
                        Log.e("getCount", "onResponse: error  " + response.errorBody().toString())
                    }

                }

                override fun onFailure(call: Call<ClickCount>, t: Throwable) {
                    Log.e("getCount", "onFailure: click count $t\n$call")
                }
            })
        }

        private fun findDifference(start_date: String, end_date: String): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var difTime = "few seconds ago"
            try {
                val d1 = sdf.parse(start_date)
                val d2 = sdf.parse(end_date)
                val differenceInTime = d2!!.time - d1!!.time
                val minutes = differenceInTime / (1000 * 60) % 60
                val hours = differenceInTime / (1000 * 60 * 60 * 24 * 7) % 52
//                val Week = difference_In_Time / (1000 * 60 * 60) % 24
                val days = differenceInTime / (1000 * 60 * 60 * 24) % 365
                val years = differenceInTime / (1000L * 60 * 60 * 24 * 365)
                var month = 0
                var week = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val firstDate = LocalDate.of(
                        start_date.substring(0, 4).toInt(),
                        start_date.substring(5, 7).toInt(),
                        start_date.substring(8, 10).toInt()
                    )
                    val secondDate = LocalDate.of(
                        end_date.substring(0, 4).toInt(),
                        end_date.substring(5, 7).toInt(),
                        end_date.substring(8, 10).toInt()
                    )
                    val difference = Period.between(firstDate, secondDate)
                    month = difference.months
                    week = (ChronoUnit.DAYS.between(firstDate, secondDate) / 7).toInt()
                }
//                Log.e("date", "findDifference: year$Years day$Days min$Minutes hour$Hours month$Month")
                difTime = if (years == 0L) {
                    if (month == 0) {
                        if (week == 0) {
                            if (days == 0L) {
                                if (hours == 0L) {
                                    "$minutes minutes ago"
                                } else {
                                    if (hours == 1L) {
                                        "$hours hour ago"
                                    } else {
                                        "$hours hours ago"
                                    }
                                }
                            } else {
                                if (days == 1L) {
                                    "$days day ago"
                                } else {
                                    "$days days ago"
                                }
                            }
                        } else {
                            if (week == 1) {
                                "$week week ago"
                            } else {
                                "$week weeks ago"
                            }
                        }
                    } else {
                        if (month == 1) {
                            "$month month ago"
                        } else {
                            "$month months ago"
                        }

                    }
                } else {
                    if (years == 1L) {
                        "$years year ago"
                    } else {
                        "$years years ago"
                    }
                } 
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return difTime
        }


        // return the number of the items in the list
        override fun getItemCount(): Int {
//            val size: Int = mList.size
            return if (mList.size > 0) {
                mList.size + (mList.size / 5).toFloat().roundToInt()
            } else 0

//            return mList.size
        }

        override fun getItemViewType(position: Int): Int {
            return if ((position + 1) % 5 == 0) {
                ADS
            } else DATA
//            return position
        }

        // Holds the views for adding it to image and text
        class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val ivmain: ImageView = itemView.findViewById(R.id.ivmain)
            val ivmainReal: ImageView = itemView.findViewById(R.id.ivmainReal)
            val ivLikeAnim: ImageView = itemView.findViewById(R.id.ivLikeAnim)
            val txtTitlemain: TextView = itemView.findViewById(R.id.txt_titlemain)
            val txtViews: TextView = itemView.findViewById(R.id.txt_views)
            val txtlikesID: TextView = itemView.findViewById(R.id.txtlikesID)
            val ivbookmark: CheckBox = itemView.findViewById(R.id.ivbookmark)
            val rvbookmark: RelativeLayout = itemView.findViewById(R.id.rvbookmark)
            val ivlike: CheckBox = itemView.findViewById(R.id.ivlike)
            val rvlike: RelativeLayout = itemView.findViewById(R.id.rvlike)
            val imghare: RelativeLayout = itemView.findViewById(R.id.imgshare)
            val imgReport: RelativeLayout = itemView.findViewById(R.id.imgReport)
            val txtPub: TextView = itemView.findViewById(R.id.txtPub)
            val txtTimeAgo: TextView = itemView.findViewById(R.id.txtTimeAgo)
            val txtTag: TextView = itemView.findViewById(R.id.txtTag)
            val relaPub: RelativeLayout = itemView.findViewById(R.id.rela_pub)
//            val interest_checkBox: CheckBox = itemView.findViewById(R.id.interest_checkBox)
        }

        class AdsHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var mRelativeFBAdmob: RelativeLayout
            private var ivAds : ImageView

            init {
                mRelativeFBAdmob = itemView.findViewById<View>(R.id.adframe) as RelativeLayout
                ivAds = itemView.findViewById<View>(R.id.iv_ads) as ImageView
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        binding.mainDataRecy.unregisterOnPageChangeCallback(this)
    }

    override fun onStop() {
        super.onStop()
        // Remove the Runnable from the Handler to avoid memory leaks
        handler.removeCallbacks(runnable)
//        refreshHandler.removeCallbacks(refreshRunnable)
//        nativead = null
//        Log.e("TAG", "onStop: handle stop  ")
    }
}
*/
