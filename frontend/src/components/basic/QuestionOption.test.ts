import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'

import QuizOption from './QuizOption.vue'

describe('QuizOption', () => {
  it('renders slot content', () => {
    const wrapper = mount(QuizOption, {
      slots: {
        default: 'Option A',
      },
    })

    expect(wrapper.text()).toContain('Option A')
  })

  it('uses default variant by default', () => {
    const wrapper = mount(QuizOption)

    expect(wrapper.find('.option-wrapper').classes()).toContain('default')
  })

  it('applies valid variant', () => {
    const wrapper = mount(QuizOption, {
      props: {
        variant: 'green',
      },
    })

    expect(wrapper.find('.option-wrapper').classes()).toContain('green')
  })

  it('falls back to default for invalid variant', () => {
    const wrapper = mount(QuizOption, {
      props: {
        variant: 'watermelon',
      },
    })

    expect(wrapper.find('.option-wrapper').classes()).toContain('default')
  })
})
