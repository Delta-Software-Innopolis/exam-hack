import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'

import BasicButton from './BasicButton.vue'

describe('BasicButton', () => {
  it('renders slot content', () => {
    const wrapper = mount(BasicButton, {
      slots: {
        default: 'Hello'
      }
    })

    expect(wrapper.text()).toContain('Hello')
  }),
  it('uses secondary variant by default', () => {
    const wrapper = mount(BasicButton)

    expect(wrapper.find('button').classes()).toContain('secondary')
  }),
  it('applies primary variant', () => {
    const wrapper = mount(BasicButton, {
      props: {
        variant: 'primary'
      }
    })

    expect(wrapper.find('button').classes()).toContain('primary')
  }),
  it('falls back to secondary for invalid variant', () => {
    const wrapper = mount(BasicButton, {
      props: {
        variant: 'watermelon'
      }
    })

    expect(wrapper.find('button').classes()).toContain('secondary')
  }),
  it('passes disabled attribute', () => {
    const wrapper = mount(BasicButton, {
      attrs: {
        disabled: true
      }
    })
    expect(wrapper.find('button').attributes('disabled')).toBeDefined()
  }),
  it('emits click event', async () => {
    const wrapper = mount(BasicButton)

    await wrapper.find('button').trigger('click')

    expect(wrapper.emitted('click')).toBeTruthy()
  })
})
