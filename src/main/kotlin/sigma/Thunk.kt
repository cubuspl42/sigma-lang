package sigma

import sigma.values.Value

interface Thunk {
    fun obtain(): Value
}
