(ns reddit-viewer.sample-data)

(def posts
  [{:permalink "/r/Catloaf/comments/6lgj64/anytime_i_leave_clothes_on_the_floor/",
    :subreddit "Catloaf",
    :title     "Anytime I leave clothes on the floor",
    :score     2517,
    :url       "https://i.redd.it/wp3soyqtxt7z.jpg"}
   {:permalink "/r/Catloaf/comments/6llznm/piano_loaf/",
    :subreddit "Catloaf",
    :title     "Piano loaf",
    :score     31,
    :url       "https://i.redd.it/mtrgzmqtcz7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6lmrk6/pumpernickel_loaf_on_the_coffee_table/",
    :subreddit "Catloaf",
    :title     "Pumpernickel loaf on the coffee table.",
    :score     15,
    :url       "http://m.imgur.com/vbTNI9y"}
   {:permalink "/r/Catloaf/comments/6llhaf/dress_loaf/",
    :subreddit "Catloaf",
    :title     "Dress loaf",
    :score     19,
    :url       "https://i.redd.it/c6g4hr6iwy7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6ln0kh/my_other_loaf_shes_11_this_month/",
    :subreddit "Catloaf",
    :title     "My other loaf. She's 11 this month.",
    :score     8,
    :url       "https://i.redd.it/u3dnwgi4408z.jpg"}
   {:permalink "/r/Catloaf/comments/6lmx5t/ella_fitzgerald_loaf/",
    :subreddit "Catloaf",
    :title     "Ella Fitzgerald Loaf",
    :score     10,
    :url       "https://i.redd.it/xswecf0t108z.jpg"}
   {:permalink "/r/Catloaf/comments/6lekoj/unevenly_baked_loaf/",
    :subreddit "Catloaf",
    :title     "Unevenly baked loaf",
    :score     218,
    :url       "https://i.redd.it/xuzq77hehs7z.jpg"}
   {:permalink "/r/Catloaf/comments/6lbo4u/this_face/",
    :subreddit "Catloaf",
    :title     "This face!",
    :score     1488,
    :url       "https://i.redd.it/knr8rmvx4p7z.jpg"}
   {:permalink "/r/Catloaf/comments/6liiiq/my_cheeseloaf/",
    :subreddit "Catloaf",
    :title     "My cheeseloaf",
    :score     22,
    :url       "https://i.redd.it/98gmjyizkv7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6lg39v/triple_loaf_baking_in_front_of_cat_tv/",
    :subreddit "Catloaf",
    :title     "Triple loaf baking in front of Cat TV",
    :score     61,
    :url       "http://i.imgur.com/ZNCwbNH.jpg"}
   {:permalink "/r/Catloaf/comments/6lhszc/loaf_life_is_rough/",
    :subreddit "Catloaf",
    :title     "Loaf life is rough.",
    :score     28,
    :url       "https://i.redd.it/pjmwv3r0yu7z.jpg"}
   {:permalink "/r/Catloaf/comments/6ldmz2/mid_meow/",
    :subreddit "Catloaf",
    :title     "Mid Meow",
    :score     121,
    :url       "https://i.redd.it/ys9mudf0nr7z.jpg"}
   {:permalink "/r/Catloaf/comments/6lk18h/thicc_loaf/",
    :subreddit "Catloaf",
    :title     "Thicc loaf",
    :score     3,
    :url       "https://i.redd.it/y9l74sx84x7z.jpg"}
   {:permalink "/r/Catloaf/comments/6li8o3/mittens_all_tucked/",
    :subreddit "Catloaf",
    :title     "Mittens all tucked!",
    :score     12,
    :url       "https://i.redd.it/u7hy6ue9cv7z.jpg"}
   {:permalink "/r/Catloaf/comments/6ljeqm/grumpy_catloaf/",
    :subreddit "Catloaf",
    :title     "grumpy catloaf",
    :score     6,
    :url       "http://imgur.com/DcIBSrF"}
   {:permalink "/r/Catloaf/comments/6lhnlj/wide_loaf/",
    :subreddit "Catloaf",
    :title     "Wide Loaf",
    :score     15,
    :url       "https://i.redd.it/0fkg6kkgtu7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6li6qb/a_big_ole_loaf_of_white_bread/",
    :subreddit "Catloaf",
    :title     "A big ole loaf of white bread",
    :score     8,
    :url       "https://i.redd.it/etrftophav7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6libdc/fresh_catloaf_warming_up_the_clean_washing/",
    :subreddit "Catloaf",
    :title     "Fresh catloaf warming up the clean washing.",
    :score     7,
    :url       "https://i.redd.it/z9wjdg1nev7z.jpg"}
   {:permalink "/r/Catloaf/comments/6leu1w/one_burnt_loaf/",
    :subreddit "Catloaf",
    :title     "One Burnt Loaf",
    :score     27,
    :url       "http://imgur.com/zh90LZW"}
   {:permalink "/r/Catloaf/comments/6ljk24/on_the_prowl_loaf/",
    :subreddit "Catloaf",
    :title     "On the prowl loaf.",
    :score     3,
    :url       "https://i.redd.it/hmprek4gkw7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6ljk1b/pakas_first_loaf_she_is_all_sass_about_this/",
    :subreddit "Catloaf",
    :title     "Pakas first loaf. She is all sass about this.",
    :score     2,
    :url       "https://i.redd.it/9g77ckufkw7z.jpg"}
   {:permalink
               "/r/Catloaf/comments/6liuqt/unhappily_under_the_bed_loaf/",
    :subreddit "Catloaf",
    :title     "unhappily under the bed loaf",
    :score     3,
    :url       "https://i.redd.it/abt6qkx0wv7z.jpg"}
   {:permalink "/r/Catloaf/comments/6ljk30/swirled_bun/",
    :subreddit "Catloaf",
    :title     "Swirled bun.",
    :score     1,
    :url       "https://i.redd.it/wjx3fm1gkw7z.jpg"}
   {:permalink "/r/Catloaf/comments/6l6zzw/cat_loaf_is_watching_you/",
    :subreddit "Catloaf",
    :title     "Cat loaf is watching you",
    :score     1249,
    :url       "https://i.redd.it/wekkdin6tk7z.jpg"}
   {:permalink "/r/Catloaf/comments/6lew8k/table_loaf/",
    :subreddit "Catloaf",
    :title     "Table loaf",
    :score     9,
    :url       "https://i.redd.it/sdwrln8fqs7z.jpg"}])