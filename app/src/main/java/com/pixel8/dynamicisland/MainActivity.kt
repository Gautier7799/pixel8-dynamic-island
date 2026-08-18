btnTest.setOnClickListener {
    DynamicIslandService.instance?.showIsland("✨ تجربة تفاعلية", "الجزيرة تعمل بنجاح مع Android 17!", "🚀", Color.parseColor("#00E676"))
    Toast.makeText(this, "تم تنشيط الجزيرة!", Toast.LENGTH_SHORT).show()
}
