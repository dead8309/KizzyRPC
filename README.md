[![CodeFactor](https://www.codefactor.io/repository/github/dead8309/kizzyrpc/badge)](https://www.codefactor.io/repository/github/dead8309/kizzyrpc) [![](https://jitpack.io/v/dead8309/KizzyRPC.svg)](https://jitpack.io/#dead8309/KizzyRPC)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)
![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=for-the-badge&logo=kotlin&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)
[![](https://dcbadge.vercel.app/api/server/vUPc7zzpV5)](https://discord.gg/vUPc7zzpV5)

# KizzyRPC

KizzyRPC is an Android library for Discord Rich Presence in Kotlin. With KizzyRPC, you can easily implement Discord Rich Presence into your Android project and make your application more immersive.

# Adding the library to your project
> Step 1. Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

> Step 2. Add the dependency

```gradle
dependencies {
    implementation 'com.github.dead8309:KizzyRPC:1.0.71'
}
```

# Usage
```kotlin
 val rpc = KizzyRPC("DISCORD ACCOUNT TOKEN")
 rpc.setActivity(
    activity = Activity(
        applicationId = "962990036020756480",
        name = "hi",
        details = "details",
        state = "state",
        type = 0,
        timestamps = Timestamps(
            start = System.currentTimeMillis(),
            end = System.currentTimeMillis() + 500000
        ),
        assets = Assets(
            largeImage = "mp:attachments/973256105515974676/983674644823412798/unknown.png",
            smallImage = "mp:attachments/973256105515974676/983674644823412798/unknown.png",
            largeText = "large-image-text",
            smallText = "small-image-text",
        ),
        buttons = listOf("Button 1", "Button 2"),
        metadata = Metadata(
            listOf(
                "https://youtu.be/1yVm_M1sKBE",
                "https://youtu.be/1yVm_M1sKBE",
            )
        )
    ),
    status = "online",
    since = System.currentTimeMillis()
)
```

## Rpc Options
| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| `activity`  | [Activity](#activity) | The activity information to be set for the Discord RPC. |
| `status` | String | The user's status, could be `online`, `idle`, `dnd` |
| `since` | Long | The Unix time (in milliseconds) when the user started the activity. |

## Activity
Required fields are marked with *
| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| `applicationId` | String | The application id of your app from discord developer portal. **_NOTE:_** `applicationId` is required if you want to use buttons |
| `name*` | String | The name of the activity that is being set. |
| `details` | String | A detailed message about the activity. |
| `state` | String | A short message about the activity. |
| `type*` | Int | The type of activity, could be 0 for `playing`, 1 for `streaming`, 2 for `listening`, 3 for `watching`, 5 for `competing`. |
| `timestamps` | [Timestamps](#timestamps) | The timestamps of the activity, including start and end time. |
| `assets` | [Assets](#assets) | The assets of the activity, including large image and text, and small image and text. |
| `buttons` | List<String> | A list of buttons labels. |
| `metadata` | [Metadata](#metadata) | Additional metadata for the activity. |

## Timestamps
| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| `start`  | Long | The Unix time (in milliseconds) when the activity started. |
| `end` | Long | The Unix time (in milliseconds) when the activity ends. |

## Assets
| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| `largeImage`  | String | The identifier of the large image asset. See [image-formatting](#image-formatting) |
| `largeText` | String | The text displayed when hovering over the large image. |
| `smallImage` | String | The identifier of the small image asset. See [image-formatting](#image-formatting)  |
| `smallText` | String | The text displayed when hovering over the small image. |

## Metadata
| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| `metadata`  | List<String> | List of button url(s). |

## Image Formatting
