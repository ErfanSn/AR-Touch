/*
 * Copyright (C) 2023 ErfanSn
 *
 * AR Touch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AR Touch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AR Touch.  If not, see <https://www.gnu.org/licenses/>.
 */

package ir.erfansn.artouch.common.util

data class Point(val x: Float, val y: Float) {

    operator fun minus(other: Point) = Point(x = x - other.x, y = y - other.y)

    operator fun plus(other: Point) = Point(x = x + other.x, y = y + other.y)

    operator fun times(factor: Float) = Point(x = x * factor, y = y * factor)
}
