# BrutusCalendar

A customizable Compose Multiplatform calendar library by William Miranda, licensed under Apache-2.0.

Display a static month or week, or browse either with a horizontal pager. Customize
the header, weekday labels, and day cells through composable lambdas.

The library uses Compose Runtime, UI, Foundation, and kotlinx-datetime, with **no
Material dependency**. The demo uses Material 3 to show a styled calendar with
navigation, date selection, and light/dark themes.

## Features

- Static monthly and weekly calendars.
- Horizontal month and week pagers with programmatic navigation.
- Customizable headers, weekday labels, and date cells.
- Configurable first weekday and optional adjacent-month dates.
- Date click callbacks and settled-page change listeners.
- Consumer-owned selection state and styling, without requiring Material.

## Supported platforms

- Android **8.0+ (API 26)**.
- iOS **15+**: ARM64 devices and Apple Silicon simulators (`iosArm64`, `iosSimulatorArm64`).
- Desktop, Web, and Intel iOS simulators are not currently targeted.

## Installation

In a Compose Multiplatform project, make sure Maven Central is available in your
`settings.gradle.kts` repositories:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

Add the library to your shared module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.wlmirand:brutus-calendar:0.1.0")
        }
    }
}
```

**Version 0.1.0 is not published yet.** The dependency above will resolve once
that release is available on Maven Central. Within this repository, use
`implementation(project(":brutus-calendar"))` instead.

The library currently uses Kotlin **2.4.21**, Compose Multiplatform **1.12.2**, and
kotlinx-datetime **0.8.0**. Use compatible Kotlin and Compose versions in your
application. A Material dependency is only needed if your custom renderers use it.

## Using the monthly calendar

The public calendar composables are in `william.miranda.brutuscalendar.view`.
Date and month values come from `kotlinx.datetime`.

### Static month

```kotlin
import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.view.MonthCalendar

@Composable
fun StaticMonthExample() {
    MonthCalendar(
        yearMonth = YearMonth(2026, 9),
        startingWeekDay = DayOfWeek.SUNDAY,
        displayExtraDays = true,
        dayOnClick = { date ->
            // Handle the date selected through the default day renderer.
        },
    )
}
```

### Scrollable months

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.model.YearMonthPagerState
import william.miranda.brutuscalendar.view.ScrollMonthCalendar

@Composable
fun ScrollableMonthExample() {
    val pagerState = remember {
        YearMonthPagerState(
            startingYearMonth = YearMonth(2026, 9),
            firstYearMonth = YearMonth(2026, 1),
            lastYearMonth = YearMonth(2026, 12),
        )
    }

    ScrollMonthCalendar(
        pagerState = pagerState,
        displayExtraDays = true,
        onMonthChangedListener = { yearMonth ->
            // Update other UI when the pager settles on a month.
        },
    )
}
```

`firstYearMonth` and `lastYearMonth` are inclusive. Supply an ordered interval
containing `startingYearMonth`. Keep the state remembered rather than constructing
a new instance on each recomposition.

This example uses `remember`: it retains the state across
recompositions, but does not provide persistence across activity or process recreation.
The library also exposes `rememberYearMonthPagerState`; its saved-state behavior
still requires platform validation before relying on it for persistence.

`onMonthChangedListener` observes `settledPage`, so it fires initially and when the
pager settles on a different page, rather than on every intermediate page during
a gesture. Replacing the listener alone does not trigger a new notification.

To navigate from buttons, call the state's suspend functions from a coroutine:

- `scrollToYearMonth` / `animateScrollToYearMonth`
- `scrollToPreviousPage` / `animateScrollToPreviousPage`
- `scrollToNextPage` / `animateScrollToNextPage`

Use `rememberCoroutineScope` for UI-triggered navigation and disable navigation
buttons at the first/last page and while scrolling.

### Display options

Both monthly composables accept:

| Parameter | Default | Behavior |
| --- | --- | --- |
| `displayHeader` | `true` | Show the month header |
| `displayWeekDaysName` | `true` | Show weekday labels |
| `startingWeekDay` | `DayOfWeek.SUNDAY` | Choose the first weekday column |
| `displayExtraDays` | `false` | Fill leading/trailing cells with adjacent-month dates |
| `dayOnClick` | `null` | Click callback used by the default day renderer |

Extra days complete the first and last weeks; they do **not** force a fixed
six-week grid. The calendar does not own selection state or automatically
navigate when an adjacent-month date is clicked.

### Custom renderers

Both monthly composables accept these slots:

| Slot | Signature | Purpose |
| --- | --- | --- |
| `headerRenderer` | `@Composable (YearMonth) -> Unit` | Month header, optionally including navigation |
| `weekDayRenderer` | `@Composable BoxScope.(DayOfWeek) -> Unit` | Weekday label |
| `dayRenderer` | `@Composable BoxScope.(DayRendererModel) -> Unit` | Date cell |

`DayRendererModel.date` is the cell's date. `calendarYearMonth` is the month being
displayed, which may differ from the cell's month when extra days are enabled.
The slots do not require Material; the default renderers use Foundation `BasicText`.

Here is a custom day renderer with externally owned selection and no Material dependency:

```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.view.MonthCalendar

@Composable
fun CustomDayExample() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    MonthCalendar(
        yearMonth = YearMonth(2026, 9),
        dayRenderer = { model ->
            BasicText(
                text = model.date.day.toString(),
                modifier = Modifier
                    .clickable { selectedDate = model.date }
                    .padding(8.dp),
                style = TextStyle(
                    fontWeight = if (selectedDate == model.date) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                ),
            )
        },
    )
}
```

**Custom day renderers own their click handling.** Passing `dayOnClick` does not
automatically make a custom renderer clickable. See `DemoDayCell` for the Material 3 version.

Keep selection or other persistent date-specific state outside individual cells.
Do not rely on a cell's local `remember` state surviving navigation between pages
or changes to the calendar layout.

## Using the weekly calendar

### Static week

`WeekCalendar` displays the week containing a reference `date`:

```kotlin
import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import william.miranda.brutuscalendar.view.WeekCalendar

@Composable
fun StaticWeekExample() {
    WeekCalendar(
        date = LocalDate(2026, 7, 1),
        startingWeekDay = DayOfWeek.MONDAY,
        displayExtraDays = true,
        dayOnClick = { date ->
            // Handle clicks through the default day renderer.
        },
    )
}
```

The example displays June 29 through July 5. The header and
`DayRendererModel.calendarYearMonth` use the reference date's month, July.
With `displayExtraDays = false`, dates outside that month are replaced with
empty cells, preserving their weekday columns. With `true`, all seven dates are shown.

### Scrollable weeks

Configure the first weekday on the state, not on `ScrollWeekCalendar`:

```kotlin
import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import william.miranda.brutuscalendar.view.ScrollWeekCalendar
import william.miranda.brutuscalendar.view.rememberWeekPagerState

@Composable
fun ScrollableWeekExample() {
    val pagerState = rememberWeekPagerState(
        firstDate = LocalDate(2026, 7, 6),
        lastDate = LocalDate(2026, 8, 2),
        startingDate = LocalDate(2026, 7, 13),
        startingDayOfWeek = DayOfWeek.MONDAY,
    )

    ScrollWeekCalendar(
        pagerState = pagerState,
        displayExtraDays = true,
        onWeekChangedListener = { date ->
            // React to the reference date of the settled page.
        },
    )
}
```

This example uses a Monday-aligned interval covering four complete weeks and
starts on its second page. `startingDate` selects the initial position;
`firstDate` and `lastDate` configure the interval. Supply an ordered interval
containing `startingDate`.

`rememberWeekPagerState` requires `startingDayOfWeek` and defaults `startingDate`
to `firstDate`. If no state is supplied to `ScrollWeekCalendar`, its default
state uses Sunday as the first weekday, January 1 of year 0 as `firstDate`, and
December 31 of year 9999 as `lastDate`. Pass an explicit state to choose a
practical interval and initial date.

`WeekPagerState.startingDayOfWeek` is the single source of the first-weekday
configuration for the pager and its rendered weeks. Unlike `WeekCalendar`,
`ScrollWeekCalendar` does **not** accept a separate `startingWeekDay` parameter.

The remembered state reconstructs the starting date, first weekday, and date
bounds. It does not save the current page or fractional scroll offset. Its saved-state behavior
still requires validation; no process-recreation guarantee is made here.
Factory arguments initialize the state rather than reactively updating an existing
instance on recomposition.

### Weekly navigation and callbacks

`WeekPagerState` exposes:

- `getPageNumberFromDate(date)`
- `getFirstDateFromPageNumber(pageNumber)`
- `scrollToDate` / `animateScrollToDate`
- `scrollToPreviousPage` / `animateScrollToPreviousPage`
- `scrollToNextPage` / `animateScrollToNextPage`

Call suspend navigation functions from a coroutine and disable previous/next
controls at the interval boundaries, as in the monthly demo.

`onWeekChangedListener` observes `settledPage`, fires initially and when the
settled page changes, and reports the same reference date used for rendering.
Replacing the listener alone does not trigger a new notification.

Despite its name, `getFirstDateFromPageNumber` currently returns a **page reference
date** computed as `firstDate + pageNumber * one week`. It is only the first
visible weekday when `firstDate` is aligned with `startingDayOfWeek`.
`WeekCalendar` expands that reference into a calendar week. For unaligned bounds,
verify navigation and initial-page mapping for your use case. Date bounds are
not a cell-level click filter; `displayExtraDays` controls adjacent-month dates,
not whether a date falls inside the pager interval.

Both weekly composables accept `displayHeader`, `displayWeekDaysName`,
`displayExtraDays`, `dayOnClick`, and the same renderer signatures as the monthly
composables. Custom renderers own their click handling and should keep persistent
selection outside the cells.

## Customization example

For a complete Material 3 example with navigation, selection, and light/dark
themes, see the demo's
[`App.kt`](demo/src/commonMain/kotlin/william/miranda/brutuscalendar/demo/App.kt)
and reusable
[`CalendarRenderers.kt`](demo/src/commonMain/kotlin/william/miranda/brutuscalendar/demo/CalendarRenderers.kt).

## License

Copyright 2026 William Miranda. Licensed under [Apache-2.0](LICENSE).
