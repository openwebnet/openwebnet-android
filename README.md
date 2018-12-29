# openwebnet-android ![openwebnet-android](images/logo.png)

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwebnet)
[![Build Status](https://travis-ci.org/openwebnet/openwebnet-android.svg)](https://travis-ci.org/openwebnet/openwebnet-android)
[![Coverage Status](https://coveralls.io/repos/github/openwebnet/openwebnet-android/badge.svg?branch=master)](https://coveralls.io/github/openwebnet/openwebnet-android?branch=master)

OpenWebNet Android client for My Home [BTicino](http://www.bticino.it/domotica-casa-connessa) and [Legrand](http://www.myopen-legrandgroup.com/) home automation system

My Home is the BTicino and Legrand home domotic system that let you manage your house from smartphones through [OpenWebNet](https://en.wikipedia.org/wiki/OpenWebNet) protocol.

Manages:
- Lighting
- Automation
- Temperature
- Sound System / Radio
- Scenario
- Power Consumption
- any OpenWebNet frame/message
- IP Camera (MJPEG)

Handle multiple gateways simultaneously with IP or Domain and password.

Backup of multiple profiles.

It isn't mandatory, but it's highly recommended that you access your domotic system using a VPN connection.

Database encrypted to increase security.

This Android application is an unofficial free and Open Source client under MIT license.

If you find a bug or have any suggestions feel free to open an issue.

If you like this app and find it useful please consider making a donation.

Additional links
* Tutorial in French [Contrôler votre système domotique MyHome de Legrand](https://devotics.fr/controler-domotique-myhome-legrand) by Jérémy Paris
* My Open Gallery [OpenWebNet](https://www.myopen-legrandgroup.com/solution-gallery/openwebnet)

*OpenWebNet is a BTicino registered trademark*

### Screenshots

<img src="images/en/screenshot-menu.png" alt="menu" height="600" /> <img src="images/en/screenshot-bs.png" alt="bs" height="600" />

<img src="images/en/screenshot-environment-fab.png" alt="environment-fab" height="600" /> <img src="images/en/screenshot-device-debug.png" alt="device-debug" height="600" />

<img src="images/en/screenshot-ipcam.png" alt="ipcam" height="600" /> <img src="images/en/screenshot-add-edit-device.png" alt="add-edit-device" height="600" />

<a href="https://play.google.com/store/apps/details?id=com.github.openwebnet&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="200"/></a>

### Changelog

All detailed [changelog](CHANGELOG.md).

### Development

```bash
./gradlew clean build
./gradlew clean test -Pbuild=dev
./gradlew app:dependencies
```

How to run [tests](images/how-to-run-tests.png) in Android Studio 

### Other contributors
- [Nicolas Moreau](https://github.com/moreau-nicolas)
- [terranum](https://github.com/terranum)
- [Denis Paris](https://github.com/parideis)

Thanks to:

* [Dagger 2](http://google.github.io/dagger)
* [Butter Knife](http://jakewharton.github.io/butterknife)
* ~~[Lombok](https://projectlombok.org)~~
* [Retrolambda](https://github.com/orfjackal/retrolambda)
* [Lightweight-Stream-API](https://github.com/aNNiMON/Lightweight-Stream-API)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [Realm](https://realm.io)
* [EventBus](https://github.com/greenrobot/EventBus)
* [ThreeTen](https://github.com/ThreeTen/threetenbp)
* [slf4j + android-logger](http://noveogroup.github.io/android-logger)
* [Robolectric](http://robolectric.org/)
* [mockito](http://mockito.org/)
* [PowerMock](https://github.com/jayway/powermock)
