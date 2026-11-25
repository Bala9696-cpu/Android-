# Android Calculator (Kotlin + Jetpack Compose)

ही **Android Studio** आणि **GitHub Actions** साठी तयार केलेली संपूर्ण **Material 3 कॅल्क्युलेटर** प्रोजेक्ट फाइल आहे.

## पद्धत १: GitHub Actions द्वारे ऑनलाइन APK बनवणे (संगणकाशिवाय)
1. GitHub वर एक नवीन Repository तयार करा.
2. ही `.zip` फाइल Extract करून आतील सर्व फाइल्स (`.github` फोल्डरसह) त्या Repository मध्ये अपलोड करा.
3. वरच्या **Actions** टॅबमध्ये जा — काही मिनिटांत बिल्ड पूर्ण होईल.
4. पूर्ण झालेल्या बिल्डवर क्लिक करून खाली **Artifacts** मधून `AndroidCalculator-debug-apk` डाउनलोड करा आणि फोनवर इन्स्टॉल करा.

## पद्धत २: Android Studio द्वारे
1. ही `.zip` फाइल Extract करून **Android Studio** मध्ये `AndroidCalculator` फोल्डर उघडा.
2. Gradle Sync पूर्ण होऊ द्या.
3. **Build > Build Bundle(s) / APK(s) > Build APK(s)** वर क्लिक करा.
